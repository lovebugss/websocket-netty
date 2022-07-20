import ws from 'k6/ws';
import {check, crpyto} from 'k6';
import proto from "./packet_pb.js"

export const options = {
    stages: [{}],
    thresholds: {},
    vus: 1,
    duration: '300s'

}

export default () => {
    const url = 'ws://localhost:18080/ws?cid=ch_00001';

    const response = ws.connect(url, {}, function (socket) {
        socket.on('open', function open() {
            console.log('connected');
            var message_proto = new proto.Data()
            message_proto.setContent("你好")
            message_proto.setType(proto.DataType.MESSAGE)
            message_proto.setTimestamp(Math.round(new Date()))
            socket.send(message_proto.serializeBinary());
            console.log(`send message: ${message_proto}`)
            socket.setInterval(function timeout() {
                socket.ping();
                console.log('Pinging every 1sec (setInterval test)');
            }, 30000);
        });

        socket.on('ping', () => console.log('PING!'));
        socket.on('pong', () => console.log('PONG!'));
        socket.on('message', function (message) {
            console.log(`Received message: ${message}`);
        });
        socket.on('close', () => console.log('disconnected'));

        socket.on('error', (e) => {
            if (e.error() != 'websocket: close sent') {
                console.log('An unexpected error occurred: ', e.error());
            }
        });

        // socket.setTimeout(function () {
        //     console.log('2 seconds passed, closing the socket');
        //     socket.close();
        // }, 2000);
    });

    check(response, {'status is 101': (r) => r && r.status === 101});
}
