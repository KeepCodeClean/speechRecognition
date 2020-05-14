<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    </head>
    <body>
        <script>
            var SpeechRecognition = (function() {

                return {
                    curImgPos: 1,
                    imagesNum: 3,

                    incrementPos: function() {
                        if (this.curImgPos >= this.imagesNum) {
                            this.curImgPos = 1;
                        } else {
                            this.curImgPos++;
                        }
                    },

                    decrementPos: function() {
                        if (this.curImgPos <= 1) {
                            this.curImgPos = this.imagesNum;
                        } else {
                            this.curImgPos--;
                        }
                    },

                    executeCommand: function (recognizedText) {
                        if (recognizedText.toLowerCase() == 'next') {
                            this.incrementPos();
                            $('#image').attr('src', '/images/' + this.curImgPos + '.jpg');
                        } else if (recognizedText.toLowerCase() == 'previous') {
                            this.decrementPos();
                            $('#image').attr('src', '/images/' + this.curImgPos + '.jpg');
                        } else {
                            alert('Can not recognize a command: ' + recognizedText);
                        }
                    },

                    initAudio: function() {
                        var self = this;
                        var audioChunks;
                        $('#record-btn').on('mousedown', function() {
                            navigator.mediaDevices.getUserMedia({
                                audio:true
                            }).then(stream => {
                                audioChunks = [];
                                self.rec = new MediaRecorder(stream);

                                self.rec.ondataavailable = e => {

                                    audioChunks.push(e.data);
                                    if (self.rec.state == "inactive"){
                                        var blob = new Blob(audioChunks, { type:'audio/x-mpeg-3' });

                                        var fd = new FormData();
                                        fd.append('blob', blob);

                                        $.ajax({
                                            url: '/recognize',
                                            type: 'POST',
                                            data: fd,
                                            cache: false,
                                            processData: false,
                                            contentType: false,
                                            success: function (data) {
                                                if (data.success) {
                                                    self.executeCommand(data.recognizedText)
                                                } else {
                                                    console.log("Could not recognize speech");
                                                }
                                            },
                                            error: function (e) {
                                                console.log("Could not recognize speech: " + e);
                                            }
                                        });
                                    }
                                }
                                self.rec.start();
                        }).catch(e => console.log(e));
                        });

                        $('#record-btn').on('mouseup', function() {
                            self.rec.stop();
                        });
                    },

                    init: function () {
                        this.initAudio();
                    }
                };
            })();

            $(window).on('load', function() {
                SpeechRecognition.init();
            });
        </script>

        <div>
            <div style="display: flex; text-align: center; flex-direction: column;">
                <h3>Speech Recognition</h3>
                <div>
                    <img id="image" src="/images/1.jpg" />
                </div>

                <div style="margin-top: 1rem;">
                    <button id="record-btn" class="btn btn-primary">Press And Command</button>
                </div>
            </div>
        </div>
    </body>

</html>

