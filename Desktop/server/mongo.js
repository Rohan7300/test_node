var mongoose = require('mongoose');
var url = process.env.MONGO_URI || "mongodb://localhost:27017/myDB"
var express = require('express');
const app = express();
const http = require('http');
const socketIo = require('socket.io');
const bodyParser = require('body-parser');
const cors = require('cors');
const fs = require('fs');


app.use(cors());
app.use(bodyParser.json());

// MongoDB Connection
mongoose.connect(url)
  .then(() => {
    console.log("DB is connected");
  })
  .catch((error) => console.error('Connection error', error));

// Message Schema Definition
const messageSchema = new mongoose.Schema({
  userId: { type: String, required: true },  // Using String for userId (can be a unique ID or placeholder)
  messageContent: { type: String, required: true },
  timestamp: { type: Date, default: Date.now },
});

const Message = mongoose.model('Message', messageSchema);

// Server and Socket.IO Setup
const server = http.createServer(app);
const io = socketIo(server);

io.on('connection', (socket) => {
  console.log("User connected");

  // Listen for the 'sendMessage' event from the client
  socket.on('sendMessage', async (messageData) => {
    const { userId, message } = messageData;

    try {
      // Save the message to the database
      const newMessage = new Message({
        userId: userId,  // Save the userId (which can be any identifier)
        messageContent: message,
      });
      await newMessage.save();

      // Emit the saved message to all connected clients
      io.emit('receiveMessage', {
        userId: userId,  // Send userId (or you could send a name or other identifier)
        content: message,
        timestamp: newMessage.timestamp,
      });
    } catch (err) {
      console.log("Error:", err);
    }
  });

  socket.on('disconnect', () => {
    console.log('A user disconnected');
  });
});

// Serve HTML file for root URL (/)
app.get('/', (req, res) => {
  fs.readFile(('first.html'), 'utf8', (err, data) => {
    if (err) {
      res.status(500).send("Error reading HTML file");
    } else {
      res.setHeader('Content-Type', 'text/html');
      res.send(data);
    }
  });
});

// Start Server
server.listen(8080, () => {
  console.log("Server is running on port 8080");
});
