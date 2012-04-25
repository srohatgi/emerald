var Ysi = require('./YsiApi').Api;
var redis = require('redis');
var events = require('events');
var emitter = new events.EventEmitter();
var clt = redis.createClient(6379,'localhost');

emitter.on('buildSession', function(obj) {
  clt.hmset('user:'+obj.authToken, 'email', obj.email, 'loginTs', obj.loginTs, function(err, reply) {
    if ( !err ) obj.res.end(JSON.stringify(obj.response,null,2)+'\n');
    else {
      console.log('error storing session info to redis');
      obj.res.end(JSON.stringify({error: 'storing session info to redis'},null,2)+'\n');
    }
  });
});

emitter.on('getSession', function(obj) {
  clt.hgetall('user:'+obj.authToken, function(err, reply) {
    if ( !err ) {
      for (var key in reply) obj[key] = reply[key]; // populate session vars
      var evt = obj.evt;
      delete obj.evt;
      emitter.emit(evt,obj);
    }
    else {
      console.log('error getting session info for: user:'+obj.authToken);
      obj.res.end(JSON.stringify({error: 'getting session info from redis'}));
    }
  });
});

emitter.on('addNetwork', function(obj) {
});

emitter.on('getNetwork', function(obj) {
});

emitter.on('getDocuments', function(obj) {
  if ( !obj.folder_id ) obj.folder_id = 0;
  Ysi('GET','/dpi/v1/folder/'+obj.folder_id,null,function(err, ret) {
    
    if ( !err ) {
      if ( obj.folder_id !== 0 ) {
        obj.res.end(JSON.stringify(ret,null,2)+'\n');
        return;
      }
      console.log(JSON.stringify(ret,null,2));
      for (var i=0;i<ret.folders.folder.length;i++) {
        if ( ret.folders.folder[i].name !== '__bin__' ) continue;
        obj.folder_id = ret.folders.folder[i].id;
        emitter.emit('getDocuments',obj);
        return;
      }
    }
    // catch all errors
    if ( !err ) err = 'user setup error: no __bin__ folder';  
    console.log('ERROR:'+err);
    obj.res.end(err);
        
  }, obj.authToken);
});

exports.index = function(req, res){
  res.render('index', { title: 'Express' })
};

exports.login = function(req, res) {
  var data = 'email='+encodeURIComponent(req.param('user'))+'&password='+encodeURIComponent(req.param('password'));
  Ysi('POST', '/dpi/v1/auth', data, function(error, response) {
    if ( !error ) {
      var obj = {
        req: req,
        res: res,
        response: response,
        authToken: response.authToken,
        email: req.param('user'),
        loginTs: new Date().getTime()
      };
      emitter.emit('buildSession', obj);
    }
    else res.end(error+'\n');
  });
};

exports.getDocuments = function(req, res) {
  emitter.emit('getSession',{ evt: 'getDocuments', req: req, res: res, authToken: req.headers['x-auth-token'] });
};

exports.getNetwork = function(req, res) {
  emitter.emit('getSession',{ evt: 'getNetwork', req: req, res: res, authToken: req.headers['x-auth-token'] });
};
