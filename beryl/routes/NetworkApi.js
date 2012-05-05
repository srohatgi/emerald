var redis = require('redis');
var clt = redis.createClient(6379,'localhost');
var events = require('events');
var emitter = new events.EventEmitter();

var Evt = function(obj, callback, next) {
  var _error = null, _data = null;
  var _that = this;
  var _next = next;
  return { 
    obj: obj, 
    cb: callback, 
    next: _next,
    data: _data,
    descr: _descr,
    next: function(next) {
      _next = next;
    },
    SetResults: function(error, data) {
      _error = err;
      _data = data;
      if ( err ) emitter.emit('error',_that);
    },
    Call: function(next) {
      emitter.emit(next);
    }
  };
}

emitter.on('error', function(evt) {
  console.log('ERROR:'+evt.descr);
  evt.cb('error');
});

var getNextMemberId = function(set, evt) {
  var member_count = set + ':members';
  clt.setnx(member_count,'0', function (err, reply) {
    if ( err ) { evt.error(err); return; }
    if ( reply === 0 ) {
      clt.incr(member_count, function(err1, reply1) {
        if ( err1 ) { evt.error(err1); return; }
        evt.obj['obj_id'] = reply;
        evt.Call(next);
      });
    }
  }
};

var UpsertObj = function(obj, name, callback) {
  var evt = Evt(obj, callback);
  if ( !obj.obj_id ) {
    evt.setData({ name: name });
    evt.next('UpsertObj');
    getNextMemberId(name,evt);
    return;
  }
  
  clt.set(name+':'+obj.obj_id,JSON.stringify(obj),function (err, reply) {
    if ( err ) { emitter.emit('error',evt.error(err)); return; }
    callback(null,obj.obj_id);
  });
};

emitter.on('UpsertObj', function(evt) {
  UpsertObj(evt.obj,evt.data['name'],evt.cb);
});

var GetObj = function(id, name, callback) {
  var evt = Evt(id,callback);
  clt.get(name+':'+id, function(err, reply) {
    if ( err ) { emitter.emit('error',evt.error(err)); return; }
    callback(null,JSON.parse(reply));
  });
}

var ObjNetwork = function(obj, set, to_id, callback) {
  var evt = Evt(obj, callback);
  clt.sadd(set, to_id, function(err, reply) {
    if ( err ) { emitter.emit('error',evt.error(err)); return; }
    callback(null,true);
  });
};

