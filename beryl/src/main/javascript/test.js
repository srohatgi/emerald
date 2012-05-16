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

emitter.on('login_done',function(logindata) {
  group.setAuthToken(logindata.authToken);
  group.browse(null,null,10,function(err,reply) {
    if ( err ) {
      console.log('error calling groups.browse():'+JSON.stringify(err,null,2));
      return;
    }
    reply.authToken = logindata.authToken;
    console.log('successfully browsed groups!');
    console.log(JSON.stringify(reply,null,2));
    emitter.emit('browse_done',reply);
  });
});
