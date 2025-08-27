import dotenv from 'dotenv';
import mqtt from 'mqtt';

dotenv.config();

const url = process.env.MQTT_URL;
const username = process.env.MQTT_USERNAME;
const password = process.env.MQTT_PASSWORD;
const intervalMs = Number(process.env.INTERVAL_MS || 5000);
const machineIds = (process.env.MACHINE_IDS || '1').split(',').map(s => s.trim());

if (!url || !username || !password) {
	console.error('Missing MQTT credentials. Set MQTT_URL, MQTT_USERNAME, MQTT_PASSWORD.');
	process.exit(1);
}

const client = mqtt.connect(url, {
	username,
	password,
	reconnectPeriod: 2000,
	rejectUnauthorized: true,
});

client.on('connect', () => {
	console.log('Simulator connected to MQTT');
});
client.on('error', (err) => {
	console.error('MQTT error:', err.message);
});

const rand = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;

class MachineState {
	constructor(id) {
		this.id = id;
		this.status = 'ON';
		this.waterLevel = 100;
		this.milkLevel = 100;
		this.beansLevel = 100;
		this.temperature = 90;
	}
	tick() {
		const dec = () => rand(3, 7);
		this.waterLevel = Math.max(0, this.waterLevel - dec());
		this.milkLevel = Math.max(0, this.milkLevel - dec());
		this.beansLevel = Math.max(0, this.beansLevel - dec());
		if (this.waterLevel <= 5) this.waterLevel = 100;
		if (this.milkLevel <= 5) this.milkLevel = 100;
		if (this.beansLevel <= 5) this.beansLevel = 100;
		this.temperature = rand(80, 100);
		if (Math.random() < 0.02) {
			this.status = this.status === 'ON' ? 'OFF' : 'ON';
		}
	}
}

const beverages = ['Espresso','Latte','Cappuccino','Americano'];
const users = ['alice','bob','charlie','dave'];

const states = new Map(machineIds.map(id => [id, new MachineState(id)]));

function publish(id, topic, payload) {
	const full = `coffeeMachine/${id}/${topic}`;
	client.publish(full, String(payload), { qos: 1 }, (err) => {
		if (err) console.error('Publish error', full, err.message);
	});
}

function sendAll() {
	for (const [id, s] of states) {
		s.tick();
		publish(id, 'status', s.status);
		publish(id, 'waterLevel', s.waterLevel);
		publish(id, 'milkLevel', s.milkLevel);
		publish(id, 'beansLevel', s.beansLevel);
		publish(id, 'temperature', s.temperature);
		if (Math.random() < 0.6 && s.status === 'ON') {
			const usage = { brewType: beverages[rand(0, beverages.length-1)], user: users[rand(0, users.length-1)], timestamp: new Date().toISOString() };
			publish(id, 'usage', usage.brewType);
		}
	}
}

setInterval(sendAll, intervalMs);