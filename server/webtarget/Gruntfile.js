module.exports = function( grunt ) {
    grunt.initConfig( {
        pkg: grunt.file.readJSON( 'package.json' ),

        // grunt clean task
        // options: https://github.com/gruntjs/grunt-contrib-clean
        clean: {
            dist: {
                options: { force: true },
                build: [ 'lib', '../src/main/webapp/lib' ]
            },
            nodeModules: ['./node_modules']
        },

        // grunt copy task - copy from node_modules to lib, then to webapp/lib
        // options: https://github.com/gruntjs/grunt-contrib-copy
        copy : {
            main : {
                files: [
                    { expand: true, cwd: 'node_modules/', src: [ '**/*' ], dest: 'lib/' },
                    { expand: true, cwd: 'lib/', src: [ '**/*', '!**/bootstrap-css-only/**' ], dest: '../src/main/webapp/lib' },
                    { expand: true, cwd: 'lib/bootstrap-css-only/', src: [ '*.css' ], dest: '../src/main/webapp/lib/bootstrap-css-only/css/' },
                    { expand: true, cwd: 'lib/bootstrap-css-only/', src: [ 'glyphicons*' ], dest: '../src/main/webapp/lib/bootstrap-css-only/fonts/' }
                ]
            }
        }
    });

    grunt.loadNpmTasks( 'grunt-contrib-clean' );
    grunt.loadNpmTasks( 'grunt-contrib-copy' );

    // Fix angular-ui-mask: replace CommonJS mask.js with browser-compatible dist/mask.js
    grunt.registerTask( 'fix-mask', 'Fix angular-ui-mask for browser', function() {
        var distMaskJs = 'lib/angular-ui-mask/dist/mask.js';
        if (grunt.file.exists(distMaskJs)) {
            // Fix in lib directory
            var maskJsLib = 'lib/angular-ui-mask/mask.js';
            grunt.log.writeln('Fixing ' + maskJsLib + ' with ' + distMaskJs);
            grunt.file.copy(distMaskJs, maskJsLib);
            // Fix in webapp/lib directory (final destination)
            var maskJsWebapp = '../src/main/webapp/lib/angular-ui-mask/mask.js';
            grunt.log.writeln('Fixing ' + maskJsWebapp + ' with ' + distMaskJs);
            grunt.file.copy(distMaskJs, maskJsWebapp);
        } else {
            grunt.log.error('dist/mask.js not found in angular-ui-mask');
        }
    });

    grunt.registerTask( 'resolve', [ 'clean:dist', 'copy', 'fix-mask' ] );
    grunt.registerTask( 'remove-node-modules', [ 'clean:nodeModules'] );
};
