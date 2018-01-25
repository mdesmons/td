var path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');


module.exports = merge(common, {
    devtool: 'inline-sourcemaps',
    output: {
    	publicPath: '/',
   	    path: path.resolve(__dirname, "build"),
        filename: 'bundle.js'
    },
    devServer: { inline: true,
        historyApiFallback: {
           index: 'index.html'
         }},
	  resolve: {
		  alias: {
			  config: path.join(__dirname, 'src', 'config', 'dev')
		  }
	  }
});
