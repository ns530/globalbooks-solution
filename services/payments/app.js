const amqp = require('amqplib');

async function consumePayments() {
    try {
        const connection = await amqp.connect('amqp://localhost');
        const channel = await connection.createChannel();

        const queue = 'payments_queue';
        await channel.assertQueue(queue, { durable: true });

        console.log('PaymentsService waiting for messages...');

        channel.consume(queue, (msg) => {
            if (msg !== null) {
                const order = JSON.parse(msg.content.toString());
                console.log('Processing payment for order:', order.id);
                // Simulate payment processing
                setTimeout(() => {
                    console.log('Payment processed for order:', order.id);
                    channel.ack(msg);
                }, 1000);
            }
        });
    } catch (error) {
        console.error('Error in PaymentsService:', error);
    }
}

consumePayments();