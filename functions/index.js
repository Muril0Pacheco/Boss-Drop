const {onSchedule} = require("firebase-functions/v2/scheduler");
const {onCall, HttpsError} = require("firebase-functions/v2/https");

const functions = require("firebase-functions/v1");

const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");
const {defineString} = require("firebase-functions/params");
const {default: axios} = require("axios");
initializeApp();
const db = getFirestore();
const messaging = getMessaging();
const itadApiKey = defineString("ISTHEREANYDEAL_KEY");
const API_URL = "https://api.isthereanydeal.com";

//  CONSTANTES DO CACHE
const CACHE_DURATION_MS = 7 * 24 * 60 * 60 * 1000; 

//  GRUPOS DE LOJA
const STORES_FILTER_ALL = "61,16,35,62,3,65,36,50,48"; 
const STORES_STEAM = "61"; 
const STORES_PRIORITY_AGGREGATORS = "36,50"; 
const STORES_OTHER_PLATFORMS = "16,35,62,3,65,48"; 

//  CONFIGURAÇÕES GERAIS
const PAGES_FOR_POPULAR = 3; 
const PAGES_FOR_STEAM = 1; 
const PAGES_FOR_PRIORITY_AGGREGATORS = 3; 
const PAGES_FOR_OTHER_PLATFORMS = 2; 
const PAGE_SIZE = 200;
const CHUNK_SIZE = 200;

//  LISTA DE JOGOS ESPECIAIS
const SPECIAL_GAMES_LIST = [
  "Baldur's Gate 3", "Hogwarts Legacy", "Elden Ring", "Cyberpunk 2077",
  "Starfield", "Diablo IV", "Helldivers 2", "Palworld", "Dragon's Dogma 2",
  "Alan Wake 2", "Grand Theft Auto V", "Red Dead Redemption 2", "The Witcher 3: Wild Hunt",
  "Call of Duty", "Stardew Valley", "Hades", "ARC Raiders", "Battlefield™ 6", "Forza Horizon 5", "EA Sports FC 26", "EA Sports FC 25"
];

function wait(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}


// BUSCA DE JOGOS COM CACHE
exports.searchGames = onCall(async (request) => {
  const queryRaw = request.data.query;
  
  if (!queryRaw || queryRaw.length < 2) {
    throw new HttpsError('invalid-argument', 'A busca deve ter pelo menos 2 caracteres.');
  }

  const queryId = queryRaw.toLowerCase().trim().replace(/\s+/g, '');

  try {
    // VIA CACHE
    const cacheRef = db.collection("search_cache").doc(queryId);
    const cacheDoc = await cacheRef.get();

    if (cacheDoc.exists) {
        const cacheData = cacheDoc.data();
        const now = Date.now();
        if ((now - cacheData.timestamp) < CACHE_DURATION_MS) {
            console.log(`Cache hit para: ${queryRaw}`);
            return cacheData.results;
        }
    }

    // VIA API
    console.log(`Cache miss para: ${queryRaw}. Chamando API externa...`);
    const response = await axios.get(`${API_URL}/games/search/v1`, {
      params: {
        key: itadApiKey.value(),
        title: queryRaw,
        limit: 20
      }
    });

    const results = response.data.map(game => ({
      id: game.id,
      title: game.title,
      slug: game.slug,
      boxart: (game.assets && game.assets.boxart) ? game.assets.boxart : null
    }));

    await cacheRef.set({
        results: results,
        timestamp: Date.now(),
        originalQuery: queryRaw
    });

    return results;

  } catch (error) {
    console.error("Erro na busca:", error);
    throw new HttpsError('internal', 'Erro ao buscar jogos na API externa.');
  }
});

exports.cleanupUserData = functions.auth.user().onDelete(async (user) => {
  const uid = user.uid;
  console.log(`Limpando dados do usuário excluído: ${uid}`);

  await db.collection("users").doc(uid).delete();

  const wishlistSnapshot = await db.collection("users").doc(uid).collection("wishlist").get();
  const batch = db.batch();
  
  wishlistSnapshot.docs.forEach((doc) => {
    batch.delete(doc.ref);
  });
  
  await batch.commit();
});


// SCHEDULER

exports.fetchBrazilianDeals = onSchedule({
  schedule: "every 2 hours",
  timeoutSeconds: 540,
  memory: "1GiB",
}, async (event) => {
  console.log("Iniciando Robô V12: Com Monitoramento de Wishlist...");

  try {
    console.log("Coletando IDs das listas de desejos dos usuários...");
    const usersSnapshot = await db.collection("users").get();
    
    let wishlistGameIds = new Set(); 
    let userWishlists = []; 
    
    usersSnapshot.forEach(doc => {
      const data = doc.data();
      

      const isPushEnabled = data.notificationsEnabled !== false; 

      if (data.favoriteGameIds && Array.isArray(data.favoriteGameIds) && data.favoriteGameIds.length > 0) {
        data.favoriteGameIds.forEach(id => wishlistGameIds.add(id));
        

        if (data.fcmToken && isPushEnabled) { 
            userWishlists.push({
                uid: doc.id,
                token: data.fcmToken,
                favorites: data.favoriteGameIds
            });
        }
      }
    });
    console.log(`Encontrados ${wishlistGameIds.size} jogos únicos nas wishlists.`);


    let specialGameIds = [];
    try {
      const lookupResponse = await axios.post(
        `${API_URL}/lookup/id/title/v1?key=${itadApiKey.value()}`,
        SPECIAL_GAMES_LIST
      );
      specialGameIds = Object.values(lookupResponse.data).filter(id => id !== null);
    } catch (e) { console.warn("Erro lookup especiais"); }
    
    let popularGameIds = [];
    for (let i = 0; i < PAGES_FOR_POPULAR; i++) {
      const res = await axios.get(`${API_URL}/stats/most-popular/v1`, {params: {key: itadApiKey.value(), limit: PAGE_SIZE, offset: i * PAGE_SIZE}});
      popularGameIds.push(...res.data.map((g) => g.id));
      await wait(500);
    }


    let dealIds = [];
    const fetchDeals = async (shops, pages) => {
        for (let i = 0; i < pages; i++) {
            const res = await axios.get(`${API_URL}/deals/v2`, {
                params: { key: itadApiKey.value(), country: "BR", lojas: shops, sort: "-cut", maduro: true, limit: PAGE_SIZE, offset: i * PAGE_SIZE }
            });
            dealIds.push(...res.data.list.map(g => g.id));
            await wait(500);
        }
    };
    await fetchDeals(STORES_STEAM, PAGES_FOR_STEAM);
    await fetchDeals(STORES_PRIORITY_AGGREGATORS, PAGES_FOR_PRIORITY_AGGREGATORS);
    await fetchDeals(STORES_OTHER_PLATFORMS, PAGES_FOR_OTHER_PLATFORMS);

    const allIds = [
      ...specialGameIds,
      ...popularGameIds,
      ...dealIds,
      ...Array.from(wishlistGameIds) 
    ];
    const uniqueGameIds = [...new Set(allIds)];
    console.log(`Total de ${uniqueGameIds.length} IDs para buscar preços.`);

    const priceChunks = chunkArray(uniqueGameIds, CHUNK_SIZE);
    let priceMap = new Map();

    for (const chunk of priceChunks) {
      const res = await axios.post(
        `${API_URL}/games/prices/v3?key=${itadApiKey.value()}&country=BR&shops=${STORES_FILTER_ALL}`,
        chunk
      );
      res.data.forEach((item) => {
        const bestDeal = item.deals.sort((a, b) => a.price.amount - b.price.amount)[0];
        if (bestDeal) {
            priceMap.set(item.id, bestDeal);
        }
      });
      await wait(2000);
    }

    let infoMap = new Map();
    for (const gameId of priceMap.keys()) {
        try {
             const res = await axios.get(`${API_URL}/games/info/v2`, {params: {key: itadApiKey.value(), id: gameId}});
             infoMap.set(gameId, res.data);
        } catch(e) {}
        await wait(50);
    }

    const promotionsCollection = db.collection("promocoes_br_v3");
    await clearCollection(promotionsCollection);
    
    const batch = db.batch();
    let notificationsToSend = [];

    priceMap.forEach((deal, gameId) => {
      const info = infoMap.get(gameId);
      
      if (info && info.assets && info.assets.boxart) {
        const popularIndex = popularGameIds.indexOf(gameId);
        const popularityRank = popularIndex !== -1 ? popularIndex + 1 : null;

        if (deal.cut > 0) {
            const finalPromotion = {
              id: info.id,
              title: info.title,
              slug: info.slug,
              popularityRank: popularityRank,
              assets: { boxart: info.assets.boxart, banner600: info.assets.banner600 || null },
              deal: { shop: deal.shop, price: deal.price, regular: deal.regular, cut: deal.cut, url: deal.url }
            };
            batch.set(promotionsCollection.doc(finalPromotion.id), finalPromotion);
        }

        // NOTIFICAÇÃO
        if (deal.cut > 0) { 
            userWishlists.forEach(user => {
                if (user.favorites.includes(gameId)) {
                    notificationsToSend.push({
                        token: user.token,
                        notification: {
                            title: 'Jogo da sua lista em oferta',
                            body: `${info.title} caiu ${deal.cut}%! Agora por R$ ${deal.price.amount}.`
                        },
                        data: {
                            gameId: gameId,
                            click_action: "OPEN_GAME_DETAIL"
                        },
                        android: {
                            priority: 'high'
                        }
                    });
                }
            });
        }
      }
    });

    await batch.commit();
    console.log("Dados salvos.");

    if (notificationsToSend.length > 0) {
        console.log(`Enviando ${notificationsToSend.length} notificações...`);
        const promises = notificationsToSend.map(msg => messaging.send(msg).catch(e => console.log("Erro push", e)));
        await Promise.all(promises);
    }

  } catch (error) {
    console.error("ERRO FATAL no Robô:", error);
  }
});

async function clearCollection(collectionRef) {
  const snapshot = await collectionRef.get();
  if (snapshot.size === 0) return;
  const batch = db.batch();
  snapshot.docs.forEach((doc) => batch.delete(doc.ref));
  await batch.commit();
}

function chunkArray(array, size) {
  const chunks = [];
  for (let i = 0; i < array.length; i += size) chunks.push(array.slice(i, i + size));
  return chunks;
}