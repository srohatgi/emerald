var https = require('https');

exports.Api = function(method,path,data,callback,auth) {
  var options = {
    host: 'test2-api.yousendit.com',
    port: 443,
    path: '/dpi/v1/auth',
    method: 'POST',
    accept: 'application/json',
    headers: {
      'X-Api-Key': '6kvyvgmc9bcqku3tmk726u4p',
      'User-Agent': 'Emerald App 1.0',
      'Content-Type': 'application/x-www-form-urlencoded',
      "Accept": "application/json"
    }
  };
  
  options.method = method;
  options.path = path;
  //console.log('AUTHCODE:'+auth);
  if ( auth ) options.headers['X-Auth-Token'] = auth;
  var apireq = https.request(options, function(apires) {
    console.log("statusCode: ", apires.statusCode);
    console.log("headers: ", apires.headers);

    var body = '';
    apires.on('data', function(d) {
      body = body + d;
    });
  
    apires.on('end', function() {
      //console.log('api response: '+body);
      if ( apires.statusCode === 200 ) {
        var read_response = JSON.parse(body);
        if ( !read_response.errorStatus ) {
          callback(null,read_response);
          return;
        }
      }
      callback(body, null);
    });
  });

  apireq.end(data);

  apireq.on('error', function(e) {
    console.error(e);
    callback(e, null);
  });
};