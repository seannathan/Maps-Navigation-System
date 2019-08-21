// map size
const MAP_SIZE = 500;

// Global reference to the canvas element.
let canvas;

// Global reference to the canvas' context.
let ctx;

let map = {};
let topleft;
let botright;
let scalex;
let scaley;
let defaultMaps = "data/maps/maps.sqlite3";
let center = [41.826891, -71.402993];
let path = [];
let traffic = {};
let trafficPaths = [];
let tempStart;
let tempEnd;
let findingPathFromStreet = false;

$(document).ready(() => {
    // Setting up the canvas.
    canvas = $('#map')[0];
    canvas.width = MAP_SIZE
    canvas.height = MAP_SIZE

    // TODO: Set up the canvas context.
    ctx = canvas.getContext("2d");
    overlay = canvas.getContext("2d");

    topleft = [41.828163, -71.404871];
    botright = [41.825541, -71.400365];

    scale(3);
    draw();
    $("#map").mouseup(pointOnClick);
    $("#submit").click(getPath);

    window.setInterval(function(){
        getTraffic();
    }, 1000);

});

$('html, body').css({
    overflow: 'hidden',
    height: '100%'
});

let dragFlag = 0;
let PageX;
let PageY;
$( "#map" ).mousemove(function( e ) {
    if(e.buttons == 1 && Math.abs(e.pageX - PageX) > 1 && Math.abs(e.pageY - PageY) > 1) {
        dragFlag = 1;

        topleft[1] = topleft[1] - descaleX(e.pageX - PageX)
        botright[1] = botright[1] - descaleX(e.pageX - PageX)

        topleft[0] = topleft[0] + descaleY(e.pageY - PageY)
        botright[0] = botright[0] + descaleY(e.pageY - PageY)

        PageX = e.pageX
        PageY = e.pageY

        draw()
    }
});

$( "#map" ).mousedown(function( e ) {
    dragFlag = 0;
    PageX = e.pageX
    PageY = e.pageY
});

$('#map').bind('mousewheel', function (e) {
    scale(1 - e.originalEvent.wheelDelta / 120 / 50);
    draw()
});

// Draws path between intersections.
function getPath() {
    let st1 = "\"" + $("#st1").val() + "\"";
    let st2 = "\"" + $("#st2").val() + "\"";
    let st3 = "\"" + $("#st3").val() + "\"";
    let st4 = "\"" + $("#st4").val() + "\"";

    if (tempStart) {
        st1 = tempStart[0];
        st2 = tempStart[1];

        if (tempEnd) {
            st3 = tempEnd[0];
            st4 = tempEnd[1];
        } else {
            const inter = getIntersection($("#st3"), $("#st4"));
            st3 = inter[0];
            st4 = inter[1];
        }
    } else if (tempEnd) {
        const inter = getIntersection($("#st1"), $("#st2"));
        st1 = inter[0];
        st2 = inter[1];
        st3 = tempEnd[0];
        st4 = tempEnd[1];
    }

    parameters = {"a": st1, "b": st2, "c": st3, "d": st4};
    findingPathFromStreet = true;
    $.post("/getRoute", parameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        findingPathFromStreet = false;
        if (responseObject.ways.length === 0) {
            alert("Path doesn't exist!");
        }

        for (let i = 0; i < responseObject.ways.length; i++) {
            path.push(responseObject.ways[i])
        }
        tempStart = undefined;
        tempEnd = undefined;
        draw();
    })
    draw();
}

// Gets intersection between two streets.
$("#inter1").click(function() {
    getIntersection($("#st1"), $("#st2"), true);
});

$("#inter2").click(function() {
    getIntersection($("#st3"), $("#st4"), false);
});

// Clears paths.
$('#clearPath').click(function (e) {
    if((tempStart && tempEnd) || findingPathFromStreet) {
        alert("Still Navigating!")
    } else {
        tempStart = undefined;
        tempEnd = undefined;
        path = [];
        draw();
    }
});

$('#load').click(function() {
    updateDatabase();
})

const pointOnClick = event => {
    if (dragFlag === 0) {
        // Get the x, y coordinates of the click event
        // with (0, 0) being the top left corner of canvas.
        const x = event.pageX
        const y = event.pageY

        const trueX = topleft[1] + descaleX(x)
        const trueY = topleft[0] - descaleY(y)

        $.post("/getNearest", {"lat": trueY, "lon": trueX}, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            coord = responseObject.point;
            const point = [parseFloat(coord[0]), parseFloat(coord[1])];
            if (!tempStart) {
                tempStart = point;
            } else if (!tempEnd) {
                tempEnd = point;
                $.post("/getRoute", {"a": tempStart[0], "b": tempStart[1], "c": point[0], "d": point[1]}, responseJSON => {
                    const responseObject = JSON.parse(responseJSON);
                    if (responseObject.ways.length === 0) {
                        alert("Path doesn't exist!");
                    } else {
                        for (let i = 0; i < responseObject.ways.length; i++) {
                            path.push(responseObject.ways[i])
                        }
                    }
                    tempStart = undefined;
                    tempEnd = undefined;
                    draw();
                });
            }
            draw();
        });
    }
};

function scale(n) {
    topleft[0] = topleft[0] - (1 - n) / 2.0 * (topleft[0] - botright[0]);
    topleft[1] = topleft[1] + (1 - n) / 2.0 * (botright[1] - topleft[1]);

    botright[0] = botright[0] + (1 - n) / 2.0 * (topleft[0] - botright[0]);
    botright[1] = botright[1] - (1 - n) / 2.0 * (botright[1] - topleft[1]);

    scalex = MAP_SIZE / Math.abs(topleft[1] - botright[1]);
    scaley = MAP_SIZE / Math.abs(topleft[0] - botright[0]);
}

function draw() {
    ctx.clearRect(0, 0, 500, 500);

    let topx = Math.floor((topleft[1] - center[1]) / 0.01);
    let topy = Math.floor((topleft[0] - center[0]) / 0.01);

    let botx = Math.floor((botright[1] - center[1]) / 0.01);
    let boty = Math.floor((botright[0] - center[0]) / 0.01);

    // Draw map.
    ctx.beginPath()
    for(let x = topx; x <= botx; x++) {
        for(let y = topy; y >= boty; y--) {
            const key = x+","+y;
            if(map[key] && map[key] != "loading") {
                for (let way of map[key]) {
                    const start = [parseFloat(way[1]), parseFloat(way[2])];
                    const end = [parseFloat(way[3]), parseFloat(way[4])];

                    if(traffic[way[0]]){
                        trafficPaths.push(way);
                    }

                    ctx.moveTo(toPixelx(start), toPixely(start));
                    ctx.lineTo(toPixelx(end), toPixely(end));
                }
            } else {
                getWays(x, y);
            }
        }
    }
    ctx.strokeStyle = 'black'
    ctx.lineWidth = 1;
    ctx.closePath();
    ctx.stroke();

    highlightTraffic();

    // Draw shortest path.
    highlightPath();
}

function highlightPath() {
    if(tempStart) {
        ctx.fillStyle = "purple";
        ctx.fillRect(toPixelx(tempStart), toPixely(tempStart), 5, 5);
    }

    if(tempEnd) {
        ctx.fillStyle = "#09F";
        ctx.fillRect(toPixelx(tempEnd), toPixely(tempEnd), 5, 5);
    }

    if ((tempStart && tempEnd) || findingPathFromStreet) {
        ctx.font = "20px Verdana";
        // Create gradient
        var gradient = ctx.createLinearGradient(350, 0, 480, 0);
        gradient.addColorStop("0", "magenta");
        gradient.addColorStop("0.5", "blue");
        gradient.addColorStop("1.0", "red");
        // Fill with gradient
        ctx.fillStyle = gradient;
        ctx.fillText("Routing...", 350, 480);
    }

    ctx.beginPath()
    for (let i = 0; i < path.length; i++) {
        way = path[i];

        const start = [parseFloat(way[0]), parseFloat(way[1])];
        const end = [parseFloat(way[2]), parseFloat(way[3])];

        ctx.moveTo(toPixelx(start), toPixely(start));
        ctx.lineTo(toPixelx(end), toPixely(end));
    }
    ctx.strokeStyle = 'blue'
    ctx.lineWidth = 5;
    ctx.closePath();
    ctx.stroke();
}

function highlightTraffic() {
    for (let way of trafficPaths) {
        ctx.beginPath();
        const start = [parseFloat(way[1]), parseFloat(way[2])];
        const end = [parseFloat(way[3]), parseFloat(way[4])];
        ctx.moveTo(toPixelx(start), toPixely(start));
        ctx.lineTo(toPixelx(end), toPixely(end));
        ctx.strokeStyle = "#"+getColor(parseFloat(traffic[way[0]]) / 10.0);
        ctx.lineWidth = 3;
        ctx.closePath();
        ctx.stroke();
    }

    trafficPaths = [];
}

function getColor(percent) {
    var color1 = 'FF0000';
    var color2 = '99ff33';
    var hex = function(x) {
        x = x.toString(16);
        return (x.length == 1) ? '0' + x : x;
    };

    var r = Math.ceil(parseInt(color1.substring(0,2), 16) * percent + parseInt(color2.substring(0,2), 16) * (1-percent));
    var g = Math.ceil(parseInt(color1.substring(2,4), 16) * percent + parseInt(color2.substring(2,4), 16) * (1-percent));
    var b = 0;

    return hex(r) + hex(g) + hex(b);
}


function toPixelx(node) {
    return (node[1] - topleft[1]) * scalex;
}

function toPixely(node) {
    return (topleft[0] - node[0]) * scaley;
}

function descaleX(x) {
	return x * 1/scalex;
}

function descaleY(y) {
	return y * 1/scaley;
}

function getWays(x, y) {
    const key = x+","+y;
    if(map[key] != "loading") {
        map[key] = "loading";
        $.post("/getWaysInBox", {"a": center[0] + 0.01 * (y + 1), "b": center[1] + 0.01 * x,
            "c": center[0] + 0.01 * y, "d": center[1] + 0.01 * (x + 1)}, responseJSON => {
                const responseObject = JSON.parse(responseJSON);
                map[key] = responseObject.ways;
                draw();
            });
    }
}

function getTraffic() {
    $.post("/getTraffic", {"a": "a"}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        traffic = responseObject;
        draw();
    });
}

function getIntersection(st1, st2, first) {
    parameters = {"st1": st1.val(), "st2": st2.val()};
    $.post("/getIntersection", parameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        const coord = responseObject.point;

        if (coord.length == 0) {
            alert("Intersection does not exist! Make sure that street name is valid.");
            return;
        }

        const point = [parseFloat(coord[0]), parseFloat(coord[1])];
        if (first) {
            tempStart = point;
            findingPathFromStreet = false;
        } else {
            tempEnd = point;
            findingPathFromStreet = false;
        }
        draw();
    })
};

// Update database if it has been changed through terminal.
function updateDatabase() {
    parameters = {"db": $("#db").val()};
    $.post("/loadDb", parameters, responseJSON => {
        alert("Check terminal to see if map was successfully loaded!");
        map = {};
        clearPath();
    })
}
