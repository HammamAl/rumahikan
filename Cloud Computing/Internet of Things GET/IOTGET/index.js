const express = require('express');
const admin = require('firebase-admin');

const app = express();
const port = 3000;

const credentials = require('./key.json');


admin.initializeApp({
  credential: admin.credential.cert(credentials),
  storageBucket: 'gs://rumahikan.appspot.com',
});


const firestore = admin.firestore();

app.get('/', (req, res) => {
  res.send('Selamat datang di server Express!');
});

app.get('/devices', async (req, res) => {
  try {
    const devicesCollection = await firestore.collection('devices').get();
    const devicesData = [];

    for (const doc of devicesCollection.docs) {
      const deviceId = doc.id;
      const devicesDataCollection = await firestore.collection('devices').doc(deviceId).collection('devicesData').get();

      const deviceData = devicesDataCollection.docs.map(dataDoc => {
        const dataId = dataDoc.id;
        const data = dataDoc.data();
        return {
          dataId,
          createdDate: data.createdDate,
          cycle: data.cycle,
          feedAmount: data.feedAmount,
          pH: data.pH,
          temperature: data.temperature,
          user: data.user,
          waterLevel: data.waterLevel
        };
      });

      devicesData.push({
        deviceId,
        deviceData
      });
    }

    res.json(devicesData);
  } catch (error) {
    console.error('Gagal mengambil data:', error);
    res.status(500).send('Gagal mengambil data dari Firestore.');
  }
});

const PORT = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Server berjalan di http://localhost:${port}`);
});
