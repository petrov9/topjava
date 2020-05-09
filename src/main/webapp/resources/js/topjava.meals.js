var mealAjaxUrl = "ajax/profile/meals/";

$.ajaxSetup({
    contents: {
        mycustomtype: "application/json",
    },
    converters: {
        "text json": function(result) {
            var array = JSON.parse(result);

            for (var i = 0; i < array.length; i++) {
                var obj = array[i];
                obj.dateTime = obj.dateTime.replace("T", " ").substring(0,16);
            }

            return array;
        }
    }
});

function updateFilteredTable() {
    // moment("12.02.2020", "dd.mm.yyyy").format("YYYY-MM-DD")

    $.ajax({
        type: "GET",
        url: mealAjaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealAjaxUrl, updateTableByData);
}

$(function () {
    setDatePicker();
    makeEditable({
        ajaxUrl: mealAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    /*"render": function(data, type, row) {
                        if (type === "display") {
                            return data.replace("T", " ").substring(0,16);
                        }

                        return data;
                    }*/
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "",
                    "orderable": false,
                    "render": renderEditBtn
                },
                {
                    "defaultContent": "",
                    "orderable": false,
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-mealExcess", data.excess)
            }
        }),
        updateTable: updateFilteredTable
    });
});

function setDatePicker () {
    var dateConfig = {
        timepicker: false,
        format: "Y.m.d"
    };
    var timeConfig = {
        datepicker: false,
        format: "H:i"
    };
    var dateTimeConfig = {
        format: "Y.m.d H:i"
    };

    $("#startDate").datetimepicker(dateConfig);
    $("#endDate").datetimepicker(dateConfig);
    $("#startTime").datetimepicker(timeConfig);
    $("#endTime").datetimepicker(timeConfig);
    $("#dateTime").datetimepicker(dateTimeConfig);
}