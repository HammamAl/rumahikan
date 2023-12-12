const Hapi = require('@hapi/hapi');
const inert = require('@hapi/inert');
const path = require('path');
const admin = require('firebase-admin');
const { db } = require('./firebase');
const collection = require('plugin/lib/node/collection');


const init = async () => {
    const server = Hapi.Server({
        host: 'localhost',
        port: 1234,
        routes: {
            files: {
                relativeTo: path.join(__dirname, 'testing')
            }
        }
    });

    await server.register([
        {
            plugin: require('hapi-geo-locate'),
            options: {
                enableByDefault: true
            }
        },
        {
            plugin: inert
        },
        {
            plugin: require('@hapi/vision')
        }
    ]);

    server.views({
        engines: {
            hbs: require('handlebars')
        },
        path: path.join(__dirname, 'views'),
        layout: 'deafault' // Perbaikan: Ganti 'deafult' menjadi 'default'
    });

    server.route([
        {
            method: 'GET',
            path: '/',
            handler: (request, h) => {
                return h.file('login.html');
            }
        },
        {
            method: 'GET',
            path: '/getusers',
            handler: async (request, h) => {
                try {
                    const usersSnapshot = await admin.firestore().collection('users').get();
                    const users = usersSnapshot.docs.map(doc => doc.data());
                    console.log(users);
                    return h.response(users);
                } catch (error) {
                    console.error(error);
                    return 'Error fetching users';
                }
            }
        },

        {
            method: 'POST',
            path: '/login',
            handler: async (request, h) => {
             try{
                const { username, password } = request.payload;
                const usersRef= admin.firestore().collection('users');
                const doc = await usersRef
                    .where('username', '==', username)
                    .where('password', '==', password)
                    .get();
                    
                return h.view('index', { username: userRecord.displayName });

            } catch(error) {
                console.error(error);
                return 'Login failed';
            }
        }
        },

    {
        method: 'POST',
        path: '/register',
        handler: async (request, h) => {
            // return h.file('register.html')
            try {
                const { username, name, password } = request.payload;
                const userRef = admin.firestore().collection('users');
                console.log('Received registration request:', { username, name, password });

                // Simpan data pengguna ke Firestore
                const newUser = {
                    username,
                    name,
                    password,
                    createdAt: admin.firestore.FieldValue.serverTimestamp()
                };

                const result = await userRef.add(newUser);
                console.log('User registered successfully:', result.id);
                return `User registered with ID: ${result.id}`;

            } catch (error) {
                console.error(error);
                return 'Error registering user';
            }
        }
        },

{
    method: 'GET',
        path: '/{any*}',
            handler: (request, h) => {
                return "<h1>Halaman tidak ditemukan</h1>";
            }
},
    ]);

await server.start();
console.log(`Server started on: ${server.info.uri}`);
}

process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});

init();
