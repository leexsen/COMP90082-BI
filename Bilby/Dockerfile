FROM node:10

# Set required environment variables
ENV NODE_ENV=production

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY /package*.json ./
COPY /client/package*.json ./client/

RUN npm run ci-all

# Bundle app source
COPY . .

RUN npm run build

ENV PORT 80
EXPOSE 80
CMD [ "npm", "run", "start" ]
