
var _blDiv,interv,w=223,h=336; // db pic w h

function createDiv() {
    if (!_blDiv) {
        _blDiv = document.createElement('div');
        _blDiv.style.display = 'none';
        _blDiv.style.position = 'absolute';
        _blDiv.style.zIndex = 100;
        document.body.appendChild(_blDiv);
        _blDiv.innerHTML = '<img src="pic/lulz/bl_1.png" />';
    }
}

function initPos() {
    _blDiv.style.left = -1000; // to hide
    _blDiv.style.display='block';
    var winHeight = window.innerHeight;
    if (!winHeight) winHeight = document.body.clientHeight;
    _blDiv.style.top = winHeight - h;
    _blDiv.style.left = -w;
}

function startSlide() {
    initPos();
    interv = setInterval(function () {moveStep(true,
            function() {
                alert('Не-не-не-не-не!!! Нет!!\nЭто Дэвид Блэйн! ФАК МОЙ МОЗГ!!');
                interv = setInterval(function () {moveStep(false)}, 50);
            }
    )}, 50);
}

function moveStep(forward, doAtEnd) {
    var step = 15;
    if (forward && _blDiv.offsetLeft < -10
            || !forward && _blDiv.offsetLeft >= -(10 + w))
        _blDiv.style.left = _blDiv.offsetLeft + (forward ? step : -step);
    else { clearInterval(interv); if (doAtEnd) doAtEnd(); }
}

function showDB() {
    createDiv();
    startSlide();
}

Array.prototype.contains = function(el) {for(var i=0;i<this.length;i++)if(this[i]==el)return true;return false;}

function dbInit() {
    var txt = document.getElementsByName("text")[0];
    if (txt) {
        var dbIntev = setInterval(function(){
            if (['david blaine','дэвид блэйн','в рот мне ноги','фак мой мозг',
                    'печеньки','оранж сода','тэдди бир'].contains(txt.value.toLowerCase())) {
                showDB(); clearInterval(dbIntev);
            }
        }, 150);
    }
}