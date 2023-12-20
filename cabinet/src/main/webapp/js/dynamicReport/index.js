DevExpress.viz.currentTheme("greenmist");
$(function() {

});

function buildPivotGrid(measures, filters) {
    var fields = new Array();
    var dataArray = new Array();
    for (i=1; i < measures.length;i++)
    {
        fields[i-1] = JSON.parse(JSON.stringify({caption: measures[i].caption, dataField: measures[i].name}));
    }

    document.getElementsByClassName("btnDisplayData")[0].onclick = function onclickBtnDisplayData() {
        function transform(list) {
            var newList = {};
            for(i =0; i < list.length;i++) {
                newList[i] = list[i].caption;
            }
            return newList;
        }

        var fullresult = {};
        fullresult.Rows = transform(fieldChooser._dataSource._descriptions.rows);
        fullresult.Filters = transform(fieldChooser._dataSource._descriptions.filters);
        fullresult.Column = transform(fieldChooser._dataSource._descriptions.columns);
        fullresult.Data = transform(fieldChooser._dataSource._descriptions.values);
        zAu.send(new zk.Event(zk.Widget.$('$divDisplayData'), "onRebuild", fullresult, {toServer:true}));
    };


    var pivotGrid = $("#pivotGrid").dxPivotGrid({
        allowSortingBySummary: true,
        allowSorting: true,
        allowFiltering: true,
        showBorders: true,
        showColumnGrandTotals: false,
        dataSource: {
            fields: fields,
            store: dataArray
        },
        fieldChooser: {
            enabled: false
        },

    }).dxPivotGrid("instance");



    var fieldChooser = $("#fieldchooser").dxPivotGridFieldChooser({
        dataSource: new DevExpress.data.PivotGridDataSource({
            fields: fields,
            store: []
        }),
        texts: {
            allFields: "Все",
            columnFields: "Колонки",
            dataFields: "Данные",
            rowFields: "Строки",
            filterFields: "Фильтры"
        },
        width: 400,
        height: 400,
        activeStateEnabled: true,

    }).dxPivotGridFieldChooser("instance");
};

function rebuildPivotGrid(jsonObject) {
    $("#fieldchooser").dxPivotGridFieldChooser().getDataSource().fields(jsonObject);
    $("#pivotGrid").dxPivotGrid().getDataSource().fields(fieldChooser.getDataSource().fields());
    $("#pivotGrid").dxPivotGrid().getDataSource().load();
}



/*
 fields: [{
 caption: "Фамилия",
 width: 120,
 dataField: "family",
 }, {
 caption: "Тип контроля",
 dataField: "typeControl",
 width: 150
 }, {
 caption: "Оценка",
 dataField: "assessment",
 width: 150,
 dataType: "number",
 summaryType: "sum",
 area: "data"
 }],


 "id": 10248,
 "assessment": "5",
 "typeControl": "Курсовая",
 "family": "Иванов"
 }, {
 "id": 10249,
 "assessment": "4",
 "typeControl": "Курсовая",
 "family": "Петров"
 }, {
 "id": 10250,
 "assessment": "5",
 "typeControl": "Практика",
 "family": "Сидоров"
 }, {
 "id": 10251,
 "assessment": "3",
 "typeControl": "Экзамен",
 "family": "Воронов"


 };*/

/*var dataArray = [{
    "id": 10248,
    "assessment": "5",
    "typeControl": "Курсовая",
    "family": "Иванов"
}, {
    "id": 10249,
    "assessment": "4",
    "typeControl": "Курсовая",
    "family": "Петров"
}, {
    "id": 10250,
    "assessment": "5",
    "typeControl": "Практика",
    "family": "Сидоров"
}, {
    "id": 10251,
    "assessment": "3",
    "typeControl": "Экзамен",
    "family": "Воронов"
}];*/