var mongoose = require('mongoose')
var url = "mongodb://localhost:27017/myDB"
var express = require('express')
const app = express()
const http = require('http')
const socketIo = require('socket.io')
const bodyParser = require('body-parser')
const { timeStamp } = require('console')
const cors = require('cors')


app.use(cors());
app.use(bodyParser.json())
mongoose.connect(url)
 .then(()=>{
    console.log("Db is connected")
 })
  .catch((error) => console.error('Connection error', error));
    
  const userschema = new mongoose.Schema({
    name: 'string',
    lastName: 'string',
    age: 'Number'
  })

  const messageSchema = new mongoose.Schema({
   userId: Number,
   messageContent: 'string'
  })
  
  const User = mongoose.model('Users', userschema)
  const message = mongoose.model('Message', messageSchema)

  const server = http.createServer(app);

  const io = socketIo(server)
  io.on('connection', (socket)=>{
    console.log("user connected")
  

  socket.on('sendMessage', async(messageData)=>{
  const {userId, message} = messageData;
  try{
    const user = await User.findById(userId)
    if(user){
      const newMessage = new Message({
        user: user._id,
        content: message
      })
      await newMessage.save()
      io.emit('receiveMessage', {
        user: user.name,
        content: message,
        timestamp: newMessage.timestamp
      });
    }
  } catch (err){
    console.log("Error")
  }
})

socket.on('disconnect', () => {
  console.log('A user disconnected');
});
  });
app.get('/items',async(req,res)=>{
  try{
    const items = await User.find();
    res.status(200).json(items);
  }
  catch(err){
    res.status(500).json({message: "Error", error:err })
  }
})

app.post('/items', async(req,res)=>{
  const { name, lastName, age } = req.body;
  const newItem = new User({name, lastName, age})
  try{
    const savedItem = await newItem.save();
    res.status(201).json(savedItem)
  }
  catch(err){
    res.status(500).json({ message: "error",error: err})
  }
})

app.post('/sendMessage', async(req,res)=>{
  const {messageContent} = req.body;
  const newUser = new message({messageContent})
  try{
   const savedItem = await newUser.save();
   res.status(201).json(savedItem)
   
  }
  catch(err){
    res.status(500).json({message: "error",error: err})
  }
})

app.listen(8080,()=>{
  console.log("you are connected 8080")
})

