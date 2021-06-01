loadPlainMap = (map) => {
    map.eachLayer(function (layer) {
        map.removeLayer(layer);
    });
    L.tileLayer('https://api.maptiler.com/maps/basic/{z}/{x}/{y}.png?key=tbqre1x94V8HBKPdH4cg', {
        attribution: '<a href="https://www.maptiler.com/copyright/" target="_blank">&copy; MapTiler</a> <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>',
        tileSize: 512,
        zoomOffset: -1,
    }).addTo(map);
}