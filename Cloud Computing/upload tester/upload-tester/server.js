const express = require('express');
const admin = require('firebase-admin');
const bodyParser = require('body-parser');

const app = express();
app.use(bodyParser.json());

const serviceAccount = require('./key.json'); 
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

app.post('/image_url', async (req, res) => {
  try {
    const { fishDiseaseImage, description } = req.body; 

    const imageCollectionRef = admin.firestore().collection('imageUrl');

    await imageCollectionRef.add({ fishDiseaseImage, description }); 

    return res.status(200).json({ message: 'Url gambar berhasil dikirim ke firestore collection.' });
  } catch (error) {
    console.error('Error:', error);
    return res.status(500).json({ error: 'Server error.' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
