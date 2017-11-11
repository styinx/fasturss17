var pat_name =  /^[A-Za-z0-9_.-]+$/;
var pat_int =  /^[0-9]+$/;
var pat_double = /^[0-9]+(\.[0-9]+)?$/;
var pat_prob = /^(0(\.\d+)?|1(\.0+)?)$/

var service_counter = 0;
var operation_counter = [];
var generator_counter = 0;
var chaosmonkey_counter = 0;

var architecture =
    {
        "simulation" : [],
        "microservices" : [],
        "generators" : [],
        "chaosmonkeys" : []
    };

function checkPattern(element, pattern, callback)
{
    if(pattern.test(element.value))
    {
        //element.style.border = "1px solid #009900";
        element.style.background = '#55CC00';
        if(callback != null)
        {
            callback();
        }
    }
    else
    {
        //element.style.border = "1px solid #990000";
        element.style.background = '#CC0000';
    }
}

function createJson()
{
    var experiment = document.getElementById('simulation-experiment').value;
    var model = document.getElementById('simulation-model').value;
    var duration = document.getElementById('simulation-duration').value;
    var report = document.getElementById('simulation-report').value;
    var datapoints = document.getElementById('simulation-datapoints').value;
    var seed = document.getElementById('simulation-seed').value;

    architecture["simulation"] = {"experiment" : experiment, "model" : model, "duration" : duration, "report" : report, "datapoints" : datapoints, "seed" : seed};

    for(var index = 0; index < service_counter; ++index)
    {
        var name = document.getElementById('microservice-' + index + '-name').value;
        var instances = document.getElementById('microservice-' + index + '-instances').value;
        var capacity = document.getElementById('microservice-' + index + '-capacity').value;

        var operations = {};
        for(var opindex = 0; opindex < operation_counter[index]; ++opindex)
        {
            var opname = document.getElementById('operation-' + index + "-" + opindex + '-name').value;
            var opdemand = document.getElementById('operation-' + index + "-" + opindex + '-demand').value;

            operations[opindex] = {"name" : opname, "demand" : opdemand};
        }

        architecture["microservices"][index] = {"name" : name, "instances" : instances, "capacity" : capacity, "operations" : operations};
    }

    for(var index = 0; index < generator_counter; ++index)
    {
        var service = document.getElementById('generator-' + index + '-service').value;
        var operation = document.getElementById('generator-' + index + '-operation').value;
        var time = document.getElementById('generator-' + index + '-time').value;

        architecture["generators"][index] = {"service" : service, "operation" : operation, "time" : time};
    }

    for(var index = 0; index < chaosmonkey_counter; ++index)
    {
        var service = document.getElementById('chaosmonkey-' + index + '-service').value;
        var instances = document.getElementById('chaosmonkey-' + index + '-instances').value;
        var time = document.getElementById('chaosmonkey-' + index + '-time').value;

        architecture["chaosmonkeys"][index] = {"service" : service, "instances" : instances, "time" : time};
    }

    var content = JSON.stringify(architecture, null, 2);
    document.getElementById('json').innerHTML = "<pre>" + content + "</pre>";
}

function copy()
{
    var element = document.getElementById('json').children[0];
    if(document.selection)
    {
        var range = document.body.createTextRange();
        range.moveToElementText(element);
        range.select();
    }
    else if(window.getSelection)
    {
        var range = document.createRange();
        range.selectNodeContents(element);
        window.getSelection().removeAllRanges();
        window.getSelection().addRange(range);
    }
    document.execCommand('copy');
}

function getServiceNames()
{
    var service_names = [];
    for(var index = 0; index < architecture["microservices"].length; ++index)
    {
        service_names.push(architecture["microservices"][index].name);
    }
    return service_names;
}

function getServiceOperations(service)
{
    var operation_names = [];
    for(var index = 0; index < architecture["microservices"][service].operations.length; ++index)
    {
        operation_names.push(architecture["microservices"][service].operations[index].name);
    }
    return operation_names;
}

function fillServiceSelects()
{
    var selects = document.getElementsByClassName('microservice-names');
    for(var index = 0; index < selects.length; ++index)
    {
        var select = selects[index];
        select.innerHTML = "";

        var names = getServiceNames()
        for(var name in names)
        {
            var option = document.createElement('option');
            option.innerHTML = names[name];
            option.value = names[name];
            select.appendChild(option);
        }
    }
}

function makeMicroservice(id)
{
    var html = ""
         + "<table id='microservice-" + id + "-table' class='microservice-table microservice-color'>"
           + "<tr>"
             + "<td>"
               + "Name:"
             + "<td>"
             + "<td>"
               + "<input id='microservice-" + id + "-name' type='text' oninput=\"createJson();checkPattern(this, pat_name, fillServiceSelects);\"/>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Instances:"
             + "<td>"
             + "<td>"
               + "<input id='microservice-" + id + "-instances' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Capacity:"
             + "<td>"
             + "<td>"
               + "<input id='microservice-" + id + "-capacity' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "<td>"
           + "</tr>"
         + "</table>"
         + "<br>"
         + "<button id='microservice-" + id + "-button-add' onclick=''>Add Microservice</button>"
         + "<button id='microservice-" + id + "-button-remove' onclick=''>Remove Microservice</button>"
         + "<div id='microservice-" + (id+1) + "-container'></div>";

    document.getElementById('microservice-' + id + '-container').innerHTML += html;
    service_counter++;
    operation_counter.push(0);
}

function makeOperation(id, service)
{
    var html = ""
         + "<table id='operation-" + service + "-" + id + "-table' class='operation-table operation-color'>"
           + "<tr>"
             + "<td>"
               + "Name:"
             + "<td>"
             + "<td>"
               + "<input id='operation-" + service + "-" + id + "-name' type='text' oninput=\"checkPattern(this, pat_name);createJson();\"/>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Demand:"
             + "<td>"
             + "<td>"
               + "<input id='operation-" + service + "-" + id + "-demand' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "<td>"
           + "</tr>"
         + "</table>"
         + "<br>"
         + "<button id='operation-" + service + "-" + id + "-button-add' onclick=''>Add Operation</button>"
         + "<button id='operation-" + service + "-" + id + "-button-remove' onclick=''>Remove Operation</button>"
         + "<div id='operation-" + service + "-" + (id+1) + "-container'></div>";

    document.getElementById('operation-' + service + "-" + id + '-container').innerHTML += html;
    operation_counter[service]++;;
}

function makeGenerator(id)
{
    var html = ""
         + "<table id='generator-" + id + "-table' class='generator-table generator-color'>"
           + "<tr>"
             + "<td>"
               + "Service:"
             + "<td>"
             + "<td>"
               + "<select class='microservice-names' id='generator-" + id + "-service' onchange=\"createJson();\"></select>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Operation:"
             + "<td>"
             + "<td>"
               + "<select id='generator-" + id + "-operation' onchange=\"createJson();\"></select>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Time:"
             + "<td>"
             + "<td>"
               + "<input id='generator-" + id + "-time' type='number' oninput=\"checkPattern(this, pat_double);createJson();\"/>"
             + "<td>"
           + "</tr>"
         + "</table>"
         + "<br>"
         + "<button id='generator-" + id + "-button-add' onclick=''>Add Generator</button>"
         + "<button id='generator-" + id + "-button-remove' onclick=''>Remove Generator</button>"
         + "<div id='generator-" + (id+1) + "-container'></div>";

    document.getElementById('generator-' + id + '-container').innerHTML += html;
    generator_counter++;
}

function makeChaosMonkey(id)
{
    var html = ""
         + "<table id='chaosmonkey-" + id + "-table' class='chaosmonkey-table chaosmonkey-color'>"
           + "<tr>"
             + "<td>"
               + "Service:"
             + "<td>"
             + "<td>"
               + "<select class='microservice-names' id='chaosmonkey-" + id + "-service' onchange=\"createJson();\"></select>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Instances:"
             + "<td>"
             + "<td>"
               + "<input id='chaosmonkey-" + id + "-instances' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "<td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Time:"
             + "<td>"
             + "<td>"
               + "<input id='chaosmonkey-" + id + "-time' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "<td>"
           + "</tr>"
         + "</table>"
          + "<br>"
          + "<button id='chaosmonkey-" + id + "-button-add' onclick=''>Add Chaosmonkey</button>"
          + "<button id='chaosmonkey-" + id + "-button-remove' onclick=''>Remove Chaosmonkey</button>"
         + "<div id='chaosmonkey-" + (id+1) + "-container'></div>";

    document.getElementById('chaosmonkey-' + id + '-container').innerHTML += html;
    chaosmonkey_counter++;
}