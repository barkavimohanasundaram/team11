var socket = null;
var gameId;
var leftAlign = 0;
$(function () {
    $("#creategame").on("click", function () {
        if ($("#NoOfPlayer").val() < 2) {
            alert("No of player need to be greater than 2");
        } else if ($("#NoOfPlayer").val() > 10) {
            alert("No of player should'nt greater than 10");
        } else {
            console.log("Connecting Request");
            socket = new WebSocket("ws://localhost:8080/MultiPlayerUnoGame/game/" + $("#GameId").val());
            gameId = $("#GameId").val();
            console.log(">>game id is " + gameId);
            socket.onopen = function () {
                console.log("connected");
            };
            socket.onmessage = function (evt) {

                if (evt.data === "Table") {
                    console.log("connect ok is comming");
                    connectOK();
                } else
                {
                    console.log("Incomming Message");
                    console.log(JSON.parse(evt.data));

                    if (JSON.parse(evt.data).ConnectType === "SendJoinedPlayerNo") {
                        getJoinedPlayerNo(JSON.parse(evt.data).Data);

                    } else if (JSON.parse(evt.data).ConnectType === "GameStartSuccess") {
                        if (JSON.parse(evt.data).Data === "Success")
                            GameStarting(JSON.parse(evt.data).playerList);

                    } else if (JSON.parse(evt.data).ConnectType === "CardsList") {
                        console.log("in showing");
                        console.log(JSON.parse(evt.data));

                        showDrawPileListList(JSON.parse(evt.data).DrawPileList);
                        showDiscardedListList(JSON.parse(evt.data).DiscardedList);

                        if (JSON.parse(evt.data).playerList)
                            GameStarting(JSON.parse(evt.data).playerList);

                    } else if (JSON.parse(evt.data).ConnectType === "PassingMessage") {
                        ShowGameMessage(JSON.parse(evt.data).Data);

                    } else if (JSON.parse(evt.data).ConnectType === "finishGameList") {
                        finishGameList(JSON.parse(evt.data).data);
                    }
                }
                console.log(">>>>>>Message came");
            };
        }
    });
    function SortByScore(a, b) {
        var aScore = a.score;
        var bScore = b.score;
        return ((aScore > bScore) ? -1 : ((aScore < bScore) ? 1 : 0));
    }
    var finishGameList = function (data) {
        $("#ShowScore").show();
        $("#inGame").hide();

        var temp = "<table><tr><td style='width: 119px;'><b>PlayerName</b></td><td><b>Score</b></td></tr>";
        data.sort(SortByScore);
        $.each(data, function (i, obj) {
            temp += "<tr><td>" + obj.playerName + "</td><td>" + obj.score + "</td></tr>";
        });
        temp += "</table>";
        $("#ShowScore").text("");
        $("#ShowScore").append(temp);
    };

    var ShowGameMessage = function (data) {
        $("#gameMessage").text(data);
    };
    var showDrawPileListList = function (data) {
        var temp;
        var leftAlign = 0;
        $("#DrawPile").text("");
        $.each(data, function (i, card) {
            temp = "<div id=\"card\" ";
            temp += "onclick='playerDrawCard(\"" + card.color + "\",\"" + card.value + "\")' ";
            temp += "style=\"left: " + leftAlign + "px;\">";
            temp += "<img src=\"img/back.png\">";
            temp += "</div>";
            $("#DrawPile").append(temp);
            leftAlign += 10;
        });
    };

    var showDiscardedListList = function (data) {
        $("#DiscardedCards").text("");
        var temp;
        leftAlign = 0;
        $.each(data, function (i, card) {

            /*temp = "<div id=\"card\" ";
             temp += "data-color=\"" + card.color + "\" ";
             temp += "data-value=\"" + card.value + "\">";
             temp += "<img src=\"img/" + card.frontimg + ".png\">";
             temp += "</div>";
             $("#DiscardedCards").append(temp);*/

            temp = "<div id=\"card\" ";
            temp += "data-color=\"" + card.color + "\" ";
            temp += "data-value=\"" + card.value + "\"";
            temp += "style=\"left: " + leftAlign + "px;\">";
            temp += "<img src=\"img/" + card.frontimg + ".png\">";
            temp += "</div>";
            $("#DiscardedCards").append(temp);
            leftAlign += 15;
        });

    };

    $("#StartGamebtn").on("click", function () {
        var msg = {
            ConnectType: "GameStart",
            ConnectBy: "Table",
            Data: $("#GameId").val()
        };
        socket.send(JSON.stringify(msg));
    });

    var GameStarting = function (data) {
        console.log("in GameStarting");
        console.log(data);
        $("#PlayerList").text("");
        var temp;
        $.each(data, function (i, player) {
            temp = "<img src='img/profile.jpg' style='width: 150px; margin-left: 15px;";
            console.log(player.turn);
            console.log(player.turn === "true");
            if (player.turn === "true") {
                temp += "padding: 1px;border: 1px solid #021a40;background-color: red;";
            }
            temp += "' onclick=drawCard('" + player.playerIndex + "') id='player' >";
            console.log(temp);
            $("#PlayerList").append(temp);
        });
        /*
         *     padding: 1px;
         border: 1px solid #021a40;
         background-color: red;
         
         * 
         */
        $("#WaitingGameDiv").hide();
        $("#inGame").show();
    };
});

var drawCard = function (playerIndex) {
    var msg = {
        ConnectType: "PlayerDrawCard",
        ConnectBy: "Table",
        PlayerIndex: playerIndex,
        GameId: gameId
    };
    socket.send(JSON.stringify(msg));
};
var playerDrawCard = function (color, value) {
    console.log("data in playerDrawCard");
    console.log(color);
    console.log(value);

};
var connectOK = function () {
    $("#CreateGameDiv").hide();
    $("#WaitingGameDiv").show();
    $("#gameIdShow").text(gameId);
    $("#playersjoined").text(0);
    var msg = {
        ConnectType: "SendDescNoPlayer",
        ConnectBy: "Table",
        Description: $("#Description").val(),
        NoOfPlayer: $("#NoOfPlayer").val()
    };
    socket.send(JSON.stringify(msg));
};
var getJoinedPlayerNo = function (data) {
    $("#playersjoined").text(data);
    if (data >= 2) {
        $("#StartGamebtn").prop('disabled', false);
    } else {
        $("#StartGamebtn").prop('disabled', true);
    }
};