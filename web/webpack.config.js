const CompressionPlugin = require(`compression-webpack-plugin`);

const path = require(`path`);
module.exports = {
  plugins: [
    new CompressionPlugin({
      test: /\.(js|css|html|svg|txt|eot|otf|ttf|gif)$/,
      filename(info) {
        let opFile = info.path.split('.'),
          opFileType = opFile.pop(),
          opFileName = opFile.join('.');
        return `${opFileName}.${opFileType}.gzip`;
      }
    })
  ]
};
