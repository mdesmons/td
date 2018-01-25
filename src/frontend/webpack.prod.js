var path = require('path');
const webpack = require('webpack');
const merge = require('webpack-merge');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const common = require('./webpack.common.js');



module.exports = merge(common, {
    output: {
     //  publicPath: './assets/',
       path: path.join(__dirname, '..', 'backend', 'resources', 'static'),
       filename: 'bundle.js'
    },
    devtool: 'sourcemaps',
	  resolve: {
		  alias: {
			  config: path.join(__dirname, 'src', 'config', 'prod')
		  }
	  },
    plugins: [
           new UglifyJSPlugin({sourceMap: true}),
	  	   new webpack.DefinePlugin({
       'process.env.NODE_ENV': JSON.stringify('production')
     })
     ],
});
