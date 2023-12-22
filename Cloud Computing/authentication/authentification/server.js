const express = require("express");
const app = express();
const jwt = require('jsonwebtoken');

const admin = require("firebase-admin");
const credentials = require("./key.json");

admin.initializeApp({
    credential: admin.credential.cert(credentials)
});

const db = admin.firestore();

app.use(express.json());
app.use(express.urlencoded({extended: true}));

app.post('/signup' ,async (req, res) => {
    try {
        const { email, name, password } = req.body;

        const snapshot = await db.collection('users').where('email', '==', email).get();
        if (!snapshot.empty) {
            return res.status(400).json({ error: 'Email sudah terdaftar sebelumnya' });
        }

        const userJson = { email, name, password };
        const docRef = await db.collection('users').add(userJson);
        res.status(201).json({ message: 'akun berhasil terdaftar', id: docRef.id });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const snapshot = await db.collection('users').where('email', '==', email).get();

        if (snapshot.empty) {
            return res.status(404).json({ error: true, message: 'User not found' });
        }

        let loggedInUser = null;

        snapshot.forEach(doc => {
            const user = doc.data();
            if (user.password === password) {
                loggedInUser = { id: doc.id, ...user };
            }
        });

        if (!loggedInUser) {
            return res.status(401).json({ error: true, message: 'Invalid credentials' });
        }

        const token = jwt.sign(
            { email: loggedInUser.email, id: loggedInUser.id },
            'yourSecretKey',
            { expiresIn: '1h' }
        );

        return res.status(200).json({
            error: false,
            message: 'success',
            loginResult: {
                userId: loggedInUser.id,
                name: loggedInUser.name,
                token: token
            }
        });
    } catch (error) {
        res.status(500).json({ error: true, message: error.message });
    }
});

app.get('/protectedRoute', (req, res) => {
    const token = req.headers.authorization;

    if (!token) {
        return res.status(401).json({ error: 'Token not provided' });
    }

    jwt.verify(token, 'yourSecretKey', (err, decoded) => {
        if (err) {
            return res.status(403).json({ error: 'Failed to authenticate token' });
        }

        // Here, you can use `decoded` to get user information from the token
        // For example: const userId = decoded.id;

        // Proceed with the protected route logic
        res.status(200).json({ message: 'Access granted to protected route' });
    });
});





const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`server is running on PORT ${PORT}.`);
})