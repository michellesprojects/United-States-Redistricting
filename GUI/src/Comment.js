class Comment{
    constructor(state, precinct, comment){
        this.state = state;
        this.precinct = precinct;
        this.comment = comment;
    }
}

postCommentToState = () =>{
    let comment = document.getElementById('new_comment_ta').value
    let commentObject = new Comment(selectedStateAbbr, null, comment)
    let xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/state/add/comment', true)
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.onload = function() {
        if (xhr.readyState == 4 && this.status == 200) {
            let response = JSON.parse(this.responseText);
            if (response != ''){
                document.getElementById('new_comment_ta').value = "Comment Added"
                selectedStateObject.comments = response
                updateCommentsList(selectedStateObject)
            } else {
                document.getElementById('new_comment_ta').value = "Failed to add comment. Try again"
            }
        }
    }
    xhr.send(JSON.stringify(commentObject))
 }

postCommentToPrecinct = () =>{
    let comment = document.getElementById('new_comment_ta').value
    let commentObject= new Comment(null, selectedPrecinctObject.geoid, comment)
    let xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/precinct/add/comment', true)
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.onload = function() {
        if (xhr.readyState == 4 && this.status == 200) {
            let response = JSON.parse(this.responseText);
            if (response != ""){
                document.getElementById('new_comment_ta').value = "Comment Added"
                selectedPrecinctObject.comments = response
                updateCommentsList(selectedPrecinctObject);
            } else {
                document.getElementById('new_comment_ta').value = "Failed to add comment. Try again"
            }
        }
    }
    xhr.send(JSON.stringify(commentObject))
}

updateCommentsList = (obj) => {
    let containerDiv = document.getElementById("comments-row");
    //remove old comments
    containerDiv.textContent = ''
   // $("#comments_list").addClass('card-panel blue-grey darken-1')
    //let outterDiv = createElem('div', 'card blue-grey darken-1','','');
    obj.comments.forEach(element => {
        let col = createElem('div','col s12','','');
        let card = createElem('div', 'card-panel blue-grey darken-1','','')
        let cardContent = createElem('div', 'card-content white-text','','')
        let cardTitle = createElem('span', 'card-title', "" + element.date.substring(0,10) + ' ' + element.date.substring(11, 19),'')
        let p = createElem('h6','',element.text,'');
        cardContent.appendChild(cardTitle);
        cardContent.appendChild(p);
        card.appendChild(cardContent)
        col.appendChild(card)
        $("#comments-row").append(col);


        /*
        let comment = document.createTextNode(element.text);
        let date = document.createTextNode("" + element.date.substring(0,10) + ' ' + element.date.substring(11, 19));
        newDiv.appendChild(date);
        newDiv.appendChild(document.createElement("br"));
        newDiv.appendChild(comment);
        newDiv.appendChild(document.createElement("br"));
        newDiv.appendChild(document.createElement("br"));

        currentDiv.appendChild(newDiv)

        */
    });
}