const amqp = require('amqplib');

async function consumeShipping() {
    try {
        const connection = await amqp.connect('amqp://localhost');
        const channel = await connection.createChannel();

        const queue = 'shipping_queue';
        await channel.assertQueue(queue, { durable: true });

        console.log('ShippingService waiting for messages...');

        channel.consume(queue, (msg) => {
            if (msg !== null) {
                const order = JSON.parse(msg.content.toString());
                console.log('Processing shipping for order:', order.id);
                // Simulate shipping processing
                setTimeout(() => {
                    console.log('Shipping processed for order:', order.id);
                    channel.ack(msg);
                }, 2000);
            }
        });
    } catch (error) {
        console.error('Error in ShippingService:', error);
    }
}

consumeShipping();