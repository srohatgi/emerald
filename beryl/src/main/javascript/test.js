var user = require('./User.js').User('localhost',8080)
   ,group = require('./Group.js').Group('localhost',8080)
   ,events = require('events')
   ,emitter = new events.EventEmitter();
   
//login
user.login(process.env.YSI_EMAIL,process.env.YSI_PASSWD,function (err,reply) {
  if ( err ) {
    console.log('error calling user.login():'+JSON.stringify(err,null,2));
    return;
  }
  console.log('successfully logged in!');
  console.log(JSON.stringify(reply,null,2));
  emitter.emit('login_done',reply);
});

emitter.on('login_done',function(data) {
  group.setAuthToken(data.authToken);
  group.addGroup('ysi_1','desc',function(err,reply) {
    if ( err ) {
      console.log('error calling groups.addGroup():'+JSON.stringify(err,null,2));
      return;
    }
    reply.authToken = data.authToken;
    console.log('successfully added group!');
    console.log(JSON.stringify(reply,null,2));
    emitter.emit('group_added',reply);
  });
});

emitter.on('group_added',function(data) {
  group.setAuthToken(data.authToken);
  group.browse(null,null,10,function(err,reply) {
    if ( err ) {
      console.log('error calling groups.browse():'+JSON.stringify(err,null,2));
      return;
    }
    reply.authToken = data.authToken;
    console.log('successfully browsed groups!');
    console.log(JSON.stringify(reply,null,2));
    emitter.emit('browse_done',reply);
  });
});
