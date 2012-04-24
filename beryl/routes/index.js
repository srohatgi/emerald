
/*
 * GET home page.
 */

exports.index = function(req, res){
  res.render('index', { title: 'Express' })
};

exports.login = function(req, res){
  var https = require('https');

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

  var apireq = https.request(options, function(apires) {
    //console.log(apireq.);
    console.log("statusCode: ", apires.statusCode);
    console.log("headers: ", apires.headers);

    var body = '';
    apires.on('data', function(d) {
      body = body + d;
    });
    
    apires.on('end', function() {
      //console.log('api response: '+body);
      res.end(body);
    });
    
  });
  
  var data = 'email='+encodeURIComponent(req.param('user'))+'&password='+encodeURIComponent(req.param('password'));  
  console.log(data);
  apireq.end(data);

  apireq.on('error', function(e) {
    console.error(e);
  });  
}