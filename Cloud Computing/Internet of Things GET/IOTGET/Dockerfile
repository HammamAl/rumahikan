# Gunakan node:14 sebagai base image
FROM node:alpine

# Set direktori kerja
WORKDIR /usr/src/app

# Salin package.json dan package-lock.json
COPY package*.json ./

# Install dependensi
RUN npm install

# Salin sisa kode proyek
COPY . .

# Port yang digunakan oleh aplikasi Express.js ( tolong di ganti aja ya mas 8080 apa 8000 ya)
EXPOSE 3000

# Command untuk menjalankan aplikasi
CMD ["node", "index.js"]
