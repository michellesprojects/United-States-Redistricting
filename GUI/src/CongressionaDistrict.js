showCongDist = () => {
    let y = 0;
    L.geoJSON(eval(selectedStateAbbr + '_cd') ,{
        onEachFeature: function(feature, layer) {
            //layer.setStyle(cong_dist_style);
            layer.setStyle({
                weight:2, 
                color: '#000000', 
                dashArray: '', 
                fillOpacity: 0.5, 
                fillColor: getColor(y++)
            });
            layer.addTo(mymap);
            cdLayersArray.push(layer);
          }
      });                 
    document.getElementById('show_cd_cb').checked = true
}

hideCongDist = () => {
    cdLayersArray.forEach(element => {
        mymap.removeLayer(element)
    });
    cdLayersArray = [];
}

showNationalParks = () => {
    let url = 'http://localhost:8080/natioal_parks?state=' + selectedStateObject.name
    let xhttp = new XMLHttpRequest();
        //let obj = {state: selectedStateObject.name, precinct: selectedPrecinctObject.geoid, errorType: errorType};
    xhttp.onreadystatechange = function(){
        if (xhttp.readyState == 4 && this.status == '200' && this.response != ''){
            let shapes = (JSON.parse(this.response))
            L.geoJSON(shapes ,{
                onEachFeature: function(feature, layer) {
                    layer.setStyle(np_dist_style);
                    layer.addTo(mymap);
                    npLayersArray.push(layer);
                }
            });                 
            $('#show_np_cb').prop('checked', true)
        }
        
    }
    xhttp.open("GET", url)
    xhttp.send();
    
}

hideNationalParks = () => {
    npLayersArray.forEach(element => {
        mymap.removeLayer(element)
    });
    pLayersArray = [];
}
