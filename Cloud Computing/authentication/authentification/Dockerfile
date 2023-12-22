FROM node:alpine
COPY package*.json /
COPY key.json ./
COPY server.js ./
RUN npm install
EXPOSE 8080
CMD ["node", "server.js"]
