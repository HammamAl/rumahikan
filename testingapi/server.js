const Hapi = require('@hapi/hapi');
const plugin = require('plugin');
const inert = require('@hapi/inert');
const path = require('path');
const { log } = require('console');
const { request } = require('http');

const init = async () => {

    const server = Hapi.Server({
        host: 'localhost',
        port: 1234,
        routes: {
            files: {
                relativeTo : path.join(__dirname,'testing')
            }
        }
    });

    await server.register([
        {
            plugin: require("hapi-geo-locate"),
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
        path: path.join(__dirname,'views'),
        layout: 'deafult'
    });

    server.route([
        {
            method: 'GET',
            path: '/',
            handler: (request, h) => {
                return h.file('test.html');
            }
        },

        {
            method: 'GET',
            path: '/dynamic',
            handler: (request, h) => {
                const data = {
                    name: 'ananta'
                }
                return h.view('index', data)
            }
        },

        {
            method: 'POST',
            path: '/login',
            handler: (request, h) => {
               return h.view('index', {username: request.payload.username});
            }    
        },
        

        {
            method: 'GET',
            path: '/download',
            handler: (request, h) => {
                return h.file('test.html',{
                    mode : 'inline',
                    filename:'welcome-download.html'
                });
            }
        },
        {
            method: 'GET',
            path: '/location',
            handler: (request, h) => {
                if (request.location) {
                    return h.view('location',{location: request.location.ip});
                } else {
                    return h.view('location',{location: "gak kebaca lokasinya"});
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
        {
            method: 'GET',
            path: '/users',
            handler: (request, h) => {
                return "<h1>menu USER</h1>"
            }
        }
    ]);

    await server.start();
    console.log(`Server started on: ${server.info.uri}`);

}

process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});

init();