const express = require('express');
const multer = require('multer');
const admin = require('firebase-admin');
const axios = require('axios');
const credentials = require('./key.json');

const app = express();

admin.initializeApp({
  credential: admin.credential.cert(credentials),
  storageBucket: 'gs://rumahikan.appspot.com',
});

const storage = multer.memoryStorage();
const fileFilter = (req, file, cb) => {
  if (file.mimetype.startsWith('image/')) {
    cb(null, true);
  } else {
    cb(new Error('Hanya gambar yang bisa dideteksi.'));
  }
};

const upload = multer({
  storage: storage,
  fileFilter: fileFilter
}).fields([
  { name: 'image', maxCount: 1 }, 
  { name: 'description', maxCount: 1 }
]);


app.post('/upload', (req, res) => {
  upload(req, res, async (err) => {
    try {
      if (err instanceof multer.MulterError) {
        return res.status(400).json({ error: err.message });
      } else if (err) {
        return res.status(500).json({ error: 'Server error.' });
      }

      const { image, description } = req.files;
      const imageFile = image[0];
      const descriptionText = description ? description[0].toString() : '';

      if (!imageFile) {
        return res.status(400).send('File not detected.');
      }

      const bucket = admin.storage().bucket();
      const fileName = `${Date.now()}_${imageFile.originalname}`;
      const file = bucket.file(fileName);

      const stream = file.createWriteStream({
        metadata: {
          contentType: imageFile.mimetype
        },
        resumable: false,
      });

      stream.on('error', (err) => {
        console.error(err);
        return res.status(500).json({ error: 'Error.' });
      });

      stream.on('finish', async () => {
        const image_url = `https://storage.googleapis.com/${bucket.name}/${file.name}`;
        
        const mlApiUrl = 'https://machine-learning-api-final-hiyfprbaoa-et.a.run.app/predict';
        
        try {
          const response = await axios.post(mlApiUrl, { image_url: image_url }, {
            headers: {
              'Content-Type': 'application/json'
            }
          }); 
          
          const predictionLabel = response.data.prediction;

    const simplifiedResponse = {
      prediction: predictionLabel
    };

          
          return res.status(200).json(simplifiedResponse);
          
        } catch (error) {
          console.error('Error sending data to ML API:', error.message);
          return res.status(500).json({ error: 'Error sending data to ML API' });
        }
      });
      
      

      stream.end(imageFile.buffer);
    } catch (error) {
      console.error(error);
      return res.status(500).json({ error: 'Server error.' });
    }
  });
});

const PORT = process.env.PORT || 8000;
app.listen(PORT, () => {
  console.log(`Server is running on PORT ${PORT}`);
});
