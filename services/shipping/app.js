'use strict';
const amqp = require('amqplib');

// FIXED: Use env-driven connection and bind to topic exchange for payment events
const AMQP_URL = process.env.AMQP_URL || 'amqp://localhost';
const AMQP_USERNAME = process.env.AMQP_USERNAME || 'guest';
const AMQP_PASSWORD = process.env.AMQP_PASSWORD || 'guest';
const EXCHANGE = process.env.AMQP_EXCHANGE || 'globalbooks.events';
const ROUTING_KEY = process.env.AMQP_ROUTING_KEY || 'globalbooks.payment.*'; // FIXED: match PaymentService routing keys
const QUEUE = process.env.AMQP_QUEUE || 'payments.events.processor'; // FIXED: durable processor queue

async function start() {
  try {
    // FIXED: Support credentials when AMQP_URL lacks them
    const url = AMQP_URL.includes('@')
      ? AMQP_URL
      : AMQP_URL.replace('amqp://', `amqp://${AMQP_USERNAME}:${AMQP_PASSWORD}@`);

    const connection = await amqp.connect(url);
    const channel = await connection.createChannel();

    // FIXED: Bind a durable queue to topic exchange for payment events
    await channel.assertExchange(EXCHANGE, 'topic', { durable: true });
    // FIXED: align queue arguments with PaymentService declaration to avoid RabbitMQ PRECONDITION_FAILED
    await channel.assertQueue(QUEUE, {
      durable: true,
      arguments: {
        'x-dead-letter-exchange': 'globalbooks.dlx', // match [RabbitMQConfig.paymentEventsQueue()](services/payment-service/src/main/java/com/globalbooks/payment/config/RabbitMQConfig.java:41)
        'x-dead-letter-routing-key': 'payments.failed'
      }
    });
    await channel.bindQueue(QUEUE, EXCHANGE, ROUTING_KEY);

    console.log(JSON.stringify({
      service: 'ShippingService',
      msg: 'Waiting for messages',
      queue: QUEUE,
      exchange: EXCHANGE,
      routingKey: ROUTING_KEY
    }));

    channel.consume(
      QUEUE,
      (msg) => {
        if (!msg) return;
        let payload;
        try {
          payload = JSON.parse(msg.content.toString());
        } catch (e) {
          payload = { raw: msg.content.toString() };
        }
        const headers = msg.properties.headers || {};
        const correlationId = headers['correlationId'] || msg.properties.correlationId || null;

        console.log(JSON.stringify({
          service: 'ShippingService',
          event: headers.eventType || 'unknown',
          correlationId,
          orderId: payload.orderId || null,
          paymentId: payload.id || null,
          status: payload.status || null,
          message: 'Processing shipping'
        }));

        // Simulate shipping processing
        setTimeout(() => {
          console.log(JSON.stringify({
            service: 'ShippingService',
            correlationId,
            message: 'Shipping processed'
          }));
          channel.ack(msg);
        }, 2000);
      },
      { noAck: false }
    );

    process.on('SIGINT', async () => {
      try {
        await channel.close();
        await connection.close();
      } finally {
        process.exit(0);
      }
    });
  } catch (error) {
    // FIXED: Structured JSON error logging + simple retry
    console.error(JSON.stringify({
      service: 'ShippingService',
      level: 'error',
      error: error.message
    }));
    setTimeout(start, 5000);
  }
}

start();