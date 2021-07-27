new Vue({
    el: '#app',
    data: {
        selected: "1",
        pos: 50,
        musicStatusCss: "music-button-start"
    },
    methods: {
        playButton: function (event) {
            if (this.musicStatusCss == "music-button-start") {
                this.musicStatusCss = "music-button-stop";
            } else {
                this.musicStatusCss = "music-button-start";
            }
        }
    }
})