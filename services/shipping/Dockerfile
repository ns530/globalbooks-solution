# syntax=docker/dockerfile:1
FROM node:20-alpine

WORKDIR /app

# Install deps with caching
COPY package.json package-lock.json* ./
RUN npm ci || npm install --only=production

# Copy source
COPY app.js ./app.js

# Environment defaults for compose
ENV AMQP_URL=amqp://rabbitmq:5672
ENV AMQP_USERNAME=guest
ENV AMQP_PASSWORD=guest
ENV AMQP_EXCHANGE=globalbooks.events
ENV AMQP_ROUTING_KEY=globalbooks.payment.*
ENV AMQP_QUEUE=payments.events.processor

CMD ["npm","start"]