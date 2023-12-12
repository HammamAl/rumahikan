const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const admin = require('firebase-admin');

const serviceAccount = require('./cred.json');

initializeApp({
    credential: cert(serviceAccount),
    databaseURL: 'https://crud-node-firebase-c3c3c.firebaseio.com'
});

const firestore = admin.firestore();
const usersCollection = firestore.collection('users');

const db = getFirestore();

module.exports = { admin, usersCollection }
module.exports = {db}