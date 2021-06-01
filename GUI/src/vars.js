
var selectedPrecinctObject = null;
var selectedStateObject = null;
var selectedStateJSON = null;
var selectedPrecinctLayer = {oldStyle:null, polygon:null};
var selectedErrors = null;
var selecterErrorType = null;
var selecterErrorToDelete = null;

var markersGroup = L.layerGroup();
var statePrecinctsGroup = L.layerGroup();
var highlightedErrorsGroup = [];

var editablePrecinctLayer = null;

var inEdit = false;
var previousEditable = null;
var newEditedShape = null;
var selectedStateAbbr = '';
var tempOldLayerStyle = null;

var stateLayersArr = [];
var cdLayersArray = []; // references to congressional district layers
var npLayersArray = [];
var precinctLayersMap = [];  // map: GEOID to layer
var highlitedNeighbors = []; //keep track of currently highlighted precincts


var tempStyleMap = [];
var tempOldStyle = {oldStyle:null, polygon:null}

var currCorrectionComment = null;

const regular_style = { weight:1,  color: '#000000', fillOpacity: 0.0 }
const neighbor_style = {weight:1,  color: '#037ffc', dashArray: '', fillOpacity: 0.2, fillColor: '#037ffc' }
const selected_style = {weight:1,  color: '#4f77bd', dashArray: '', fillOpacity: 0.7, fillColor: '#4f77bd'}
const available_state = {weight:2, color: '#000000', dashArray: '', fillOpacity: 0.5, fillColor: '#34ebcf'}
const cong_dist_style = {weight: 4, color: '#000000', dashArray: '', fillOpacity: 0.0 }
const np_dist_style = {weight: 2, color: '#000000', dashArray: '', fillOpacity: 0.3, fillColor: '#2aaf54' }
const mouse_over_style = {weight: 4, color: "#000000", fillOpacity: 0.7}
const selected_to_operate = {weight:2, color: '#000000', dashArray: '', fillOpacity: 0.5, fillColor: '#34ebcf'};
const precinct_error_style = {weight:2, color: '#000000', dashArray: '', fillOpacity: 0.5, fillColor: '#f3ef0a'};


intToCommaInt = (x) => {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

capitalizeFirstLetter = (string) => {
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
  }
  