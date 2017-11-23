var pat_name =  /^[A-Za-z0-9_.-]+$/;
var pat_int =  /^[0-9]+$/;
var pat_double = /^[0-9]+(\.[0-9]+)?$/;
var pat_prob = /^(0(\.\d+)?|1(\.0+)?)$/;
var pat_array = /^([0-9]+(\.[0-9]+)?)+(\,[0-9]+(\.[0-9]+)?)*$/;

var service_counter = 0;
var operation_counter = [];
var pattern_counter = [];
var generator_counter = 0;
var chaosmonkey_counter = 0;

var architecture =
    {
        "simulation" : [],
        "microservices" : [],
        "generators" : [],
        "chaosmonkeys" : []
    };

function selectValues(select)
{
    var vals = [];
    for (var i = 0; i < select.options.length; i++)
    {
        if(select.options[i].selected == true)
        {
            vals.push(select.options[i].value);
        }
    }
    return vals;
}

function checkPattern(element, pattern, callback)
{
    if(pattern.test(element.value))
    {
        element.style.background = '#55CC00';
        if(callback != null)
        {
            callback();
        }
    }
    else
    {
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

    architecture["microservices"] = [];
    for(var index = 0; index < service_counter; ++index)
    {
        var name = document.getElementById('microservice-' + index + '-name').value;
        var instances = document.getElementById('microservice-' + index + '-instances').value;
        var capacity = document.getElementById('microservice-' + index + '-capacity').value;

        var patterns = [];

        for(var service_pattern_index = 0; service_pattern_index < pattern_counter[service_counter]; service_pattern_index++)
        {

        }

//        var pattern = {};
//        var patternName = document.getElementById('microservice-' + index + '-patternName').value;
//        var patternValue = document.getElementById('microservice-' + index + '-patternValue').value;
//
//        pattern[patternName] = patternValue;
//        if(pattern[patternName] !== "")
//            patterns.push(pattern);

        var operations = [];
        for(var opindex = 0; opindex < operation_counter[index]; ++opindex)
        {
            var opname = document.getElementById('operation-' + index + "-" + opindex + '-name').value;
            var opdemand = document.getElementById('operation-' + index + "-" + opindex + '-demand').value;
            var oppatterns = selectValues(document.getElementById('operation-' + index + "-" + opindex + '-patterns'));

            var opdependencies = [];
            var opdependentService = document.getElementById('operation-' + index + "-" + opindex + '-dependentService').value;
            var opdependentOperation = document.getElementById('operation-' + index + "-" + opindex + '-dependentOperation').value;
            var opdependentProbability = document.getElementById('operation-' + index + "-" + opindex + '-dependentProbability').value;

            opdependencies.push({"service" : opdependentService, "operation" : opdependentOperation, "probability" : opdependentProbability});

            operations.push({"name" : opname, "demand" : opdemand, "patterns" : oppatterns, "dependencies" : opdependencies});
        }

        architecture["microservices"][index] = {"name" : name, "instances" : instances, "capacity" : capacity, "patterns" : patterns, "operations" : operations};
    }

    architecture["generators"] = [];
    for(var index = 0; index < generator_counter; ++index)
    {
        var service = document.getElementById('generator-' + index + '-service').value;
        var operation = document.getElementById('generator-' + index + '-operation').value;
        var time = document.getElementById('generator-' + index + '-time').value;

        architecture["generators"][index] = {"service" : service, "operation" : operation, "time" : time};
    }

    architecture["chaosmonkeys"] = [];
    for(var index = 0; index < chaosmonkey_counter; ++index)
    {
        var service = document.getElementById('chaosmonkey-' + index + '-service').value;
        var instances = document.getElementById('chaosmonkey-' + index + '-instances').value;
        var time = document.getElementById('chaosmonkey-' + index + '-time').value;

        architecture["chaosmonkeys"][index] = {"service" : service, "instances" : instances, "time" : time};
    }

    var content = JSON.stringify(architecture, null, 2);
    var mic_pos = content.indexOf('microservices');
    var gen_pos = content.indexOf('generators');
    var mon_pos = content.indexOf('chaosmonkeys');

    var sim = content.substr(4, mic_pos - 9);
    var mic = content.substr(mic_pos, gen_pos - mic_pos - 5);
    var gen = content.substr(gen_pos, mon_pos - gen_pos - 5);
    var mon = content.substr(mon_pos, content.length - mon_pos - 2);

    var op_pos = mic.indexOf('operations');
    var ser_num = 0;
    while(op_pos > -1)
    {
        var brack_pos = mic.indexOf(']\n    }', op_pos+1);
        if(brack_pos > -1)
        {
            mic = mic.substr(0, op_pos - 1)
                + "<span class='operation-color'>\"" + mic.substr(op_pos, brack_pos + 1 - op_pos) + "</span>"
                + mic.substr(brack_pos + 1);
        }
        op_pos = mic.indexOf('operations', brack_pos + 100);
        ser_num++;
    }

    content = "{\n&nbsp;&nbsp;<span class='simulation-color'>" + sim + "</span>,\n&nbsp;&nbsp;"
        + "<span class='microservice-color'>\"" + mic + "</span>,\n&nbsp;&nbsp;"
        + "<span class='generator-color'>\"" + gen + "</span>,\n&nbsp;&nbsp;"
        + "<span class='chaosmonkey-color'>\"" + mon + "</span>\n}";
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
    for(var service_id = 0; service_id < architecture["microservices"].length; ++service_id)
    {
        if(service === architecture["microservices"][service_id].name)
        {
            for(var index = 0; index < architecture["microservices"][service_id].operations.length; ++index)
            {
                operation_names.push(architecture["microservices"][service_id].operations[index].name);
            }
        }
    }
    return operation_names;
}

function fillServiceSelects()
{
    var selects = document.getElementsByClassName('microservice-names');
    for(var index = 0; index < selects.length; ++index)
    {
        var service_names = getServiceNames();
        fillSelect(selects[index], service_names);
    }
}

function fillOperationSelect(service_select, operation_select_id)
{
    var operation_names = getServiceOperations(service_select.value);
    var operation_select = document.getElementById(operation_select_id);
    fillSelect(operation_select, operation_names);
}

function fillSelect(element, name_values)
{
    var select = element;
    var select_value = select.value;
    select.innerHTML = "";

    var names = name_values
    for(var name in names)
    {
        var option = document.createElement('option');
        option.innerHTML = names[name];
        option.value = names[name];
        if(select_value === names[name])
            option.selected = true;
        select.appendChild(option);
    }
}

function makeMicroservice(id)
{
    var html = ""
         + "<table id='microservice-" + id + "-table' class='microservice-table microservice-color'>"
           + "<tr>"
             + "<td>"
               + "Name:"
             + "</td>"
             + "<td>"
               + "<input id='microservice-" + id + "-name' type='text' oninput=\"createJson();checkPattern(this, pat_name, fillServiceSelects);\"/>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Instances:"
             + "</td>"
             + "<td>"
               + "<input id='microservice-" + id + "-instances' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Capacity:"
             + "</td>"
             + "<td>"
               + "<input id='microservice-" + id + "-capacity' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Patterns:"
             + "</td>"
             + "<td>"
               + "<div id='microservice-" + id + "-pattern-0'></div>"
               + "<button>Add Pattern</button>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "<h3>Operations:</h3>"
             + "</td>"
             + "<td>"
               + "&nbsp;"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "&nbsp;"
             + "</td>"
             + "<td>"
               + "<div id='operation-" + id + "-0-container'></div>"
             + "</td>"
           + "</tr>"
         + "</table>"
         + "<br>"
         + "<button id='microservice-" + id + "-button-add' onclick=\"makeMicroservice(" + (id+1) + ");createJson();\">Add Microservice</button>";

    if(service_counter > 0)
    {
        html += "<button id='microservice-" + id + "-button-remove' onclick=\"removeMicroservice(" + id + ");createJson();\">Remove Microservice</button>";
    }
    if(service_counter == 1)
    {
        document.getElementById('microservice-' + (id-1) + '-button-add').style.display = "none";
    }
    else if(service_counter > 1)
    {
        document.getElementById('microservice-' + (id-1) + '-button-add').style.display = "none";
        document.getElementById('microservice-' + (id-1) + '-button-remove').style.display = "none";
    }

    html += "<div id='microservice-" + (id+1) + "-container'></div>";

    document.getElementById('microservice-' + id + '-container').innerHTML += html;
    makePattern(0, id, null);
    makeOperation(0, id);
    pattern_counter[service_counter] = 0;
    operation_counter[service_counter] = 1;
    service_counter++;
}

function removeMicroservice(id)
{
    if(service_counter == 2)
    {
        document.getElementById('microservice-' + (id-1) + '-button-add').style.display = "";
    }
    else if(service_counter > 2)
    {
        document.getElementById('microservice-' + (id-1) + '-button-add').style.display = "";
        document.getElementById('microservice-' + (id-1) + '-button-remove').style.display = "";
    }

    document.getElementById('microservice-' + id + '-container').innerHTML = "";
    service_counter--;
    operation_counter.splice(-1, 1);
}

function makePattern(id, service, operation)
{
    var html = "";
    if(operation === null)
    {
        html = ""
            + "<select id='microservice-" + service + "-pattern-" + id + "'>"
                + "<option value=''></option>"
                + "<option value='Thread Pool'>Thread Pool</option>"
                + "<option value='Thread Queue'>Thread Queue</option>"
            + "</select>"
            + "<div id='microservice-" + service + "-pattern-" + (id+1) + "'></div>";

        document.getElementById('microservice-' + service + '-pattern-' + id).innerHTML = html;
        pattern_counter[service]++;
    }
    else
    {
        html = ""
            + "<select id='operation-" + service + "-" + operation + "-pattern-" + id + "'>"
                + "<option value=''></option>"
                + "<option value='Circuit Breaker'>Circuit Breaker</option>"
            + "</select>"
            + "<div id='operation-" + service + "-" + operation + "-pattern-" + (id+1) + "'></div>";
        document.getElementById('operation-' + service + '-' + operation + '-pattern-' + id).innerHTML = html;
        pattern_counter[service][operation]++;
    }
}

function removePattern(id, service, operation)
{
    if(pattern_counter[service][operation] == 1)
    {
        document.getElementById('microservice-' + service + "-" + operation + "-" + (id-1) + '-button-add').style.display = "";
        pattern_counter[service]--;
    }
    else if(pattern_counter[service][operation] > 1)
    {
        document.getElementById('microservice-' + service + "-" + operation + "-" + (id-1) + '-button-add').style.display = "";
        document.getElementById('microservice-' + service + "-" + operation + "-" + (id-1) + '-button-remove').style.display = "";
        pattern_counter[service][operation]--;
    }

    document.getElementById('microservice-' + service + "-" + operation + "-" + (id-1) + '-container').innerHTML = "";
}

function makeOperation(id, service)
{
    var html = ""
         + "<table id='operation-" + service + "-" + id + "-table' class='operation-table operation-color'>"
           + "<tr>"
             + "<td>"
               + "Name:"
             + "</td>"
             + "<td>"
               + "<input id='operation-" + service + "-" + id + "-name' type='text' oninput=\"checkPattern(this, pat_name);createJson();\"/>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Demand:"
             + "</td>"
             + "<td>"
               + "<input id='operation-" + service + "-" + id + "-demand' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Patterns:"
             + "</td>"
             + "<td>"
               + "<select multiple id='operation-" + service + "-" + id + "-patterns' onchange=\"createJson();\">"
                 + "<option value='Circuit Breaker'>Circuit Breaker</option>"
               + "</select>"
               + "<br><button>Add Pattern</button>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Dependencies:"
             + "</td>"
             + "<td>"
               + "<select class='microservice-names' id='operation-" + service + "-" + id + "-dependentService' onchange=\"fillOperationSelect(this, 'operation-" + service + "-" + id + "-dependentOperation');createJson();\"></select>"
               + "<select class='operation-names' id='operation-" + service + "-" + id + "-dependentOperation'></select>"
               + "<input id='operation-" + service + "-" + id + "-dependentProbability' type='number' oninput=\"checkPattern(this, pat_prob);createJson();\"/>"
             + "</td>"
           + "</tr>"
         + "</table>"
         + "<br>"
         + "<button id='operation-" + service + "-" + id + "-button-add' onclick=\"makeOperation(" + (id+1) + ", " + service + ");createJson();\">Add Operation</button>";

    if(operation_counter[service] > 0)
         html += "<button id='operation-" + service + "-" + id + "-button-remove' onclick=\"removeOperation(" + id + ", " + service + ");createJson();\">Remove Operation</button>";

    if(operation_counter[service] == 1)
    {
        document.getElementById('operation-' + service + "-" + (id-1) + '-button-add').style.display = "none";
    }
    else if(operation_counter[service] > 1)
    {
        document.getElementById('operation-' + service + "-" + (id-1) + '-button-add').style.display = "none";
        document.getElementById('operation-' + service + "-" + (id-1) + '-button-remove').style.display = "none";
    }

    html += "<div id='operation-" + service + "-" + (id+1) + "-container'></div>";

    document.getElementById('operation-' + service + '-' + id + '-container').innerHTML += html;
    operation_counter[service]++;
}

function removeOperation(id, service)
{
    if(operation_counter[service] == 2)
    {
        document.getElementById('operation-' + service + '-' + (id-1) + '-button-add').style.display = "";
    }
    else if(operation_counter[service] > 2)
    {
        document.getElementById('operation-' + service + '-' + (id-1) + '-button-add').style.display = "";
        document.getElementById('operation-' + service + '-' + (id-1) + '-button-remove').style.display = "";
    }

    document.getElementById('operation-' + service + '-' + id + '-container').innerHTML = "";
    operation_counter[service]--;
}

function makeGenerator(id)
{
    var html = ""
         + "<table id='generator-" + id + "-table' class='generator-table generator-color'>"
           + "<tr>"
             + "<td>"
               + "Service:"
             + "</td>"
             + "<td>"
               + "<select class='microservice-names' id='generator-" + id + "-service' onchange=\"createJson();fillOperationSelect(this,'generator-" + id + "-operation');\"></select>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Operation:"
             + "</td>"
             + "<td>"
               + "<select class='operation-names' id='generator-" + id + "-operation' onchange=\"createJson();\"></select>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Time:"
             + "</td>"
             + "<td>"
               + "<input id='generator-" + id + "-time' type='number' oninput=\"checkPattern(this, pat_double);createJson();\"/>"
             + "</td>"
           + "</tr>"
         + "</table>"
         + "<br>"
         + "<button id='generator-" + id + "-button-add' onclick=\"makeGenerator(" + (id+1) + ");createJson();\">Add Generator</button>";

    if(generator_counter > 0)
         html += "<button id='generator-" + id + "-button-remove' onclick=\"removeGenerator(" + id + ");createJson();\">Remove Generator</button>";

    if(generator_counter == 1)
    {
        document.getElementById('generator-' + (id-1) + '-button-add').style.display = "none";
    }
    else if(generator_counter > 1)
    {
        document.getElementById('generator-' + (id-1) + '-button-add').style.display = "none";
        document.getElementById('generator-' + (id-1) + '-button-remove').style.display = "none";
    }

    html += "<div id='generator-" + (id+1) + "-container'></div>";

    document.getElementById('generator-' + id + '-container').innerHTML += html;
    generator_counter++;
}

function removeGenerator(id)
{
    if(generator_counter == 2)
    {
        document.getElementById('generator-' + (id-1) + '-button-add').style.display = "";
    }
    else if(generator_counter > 2)
    {
        document.getElementById('generator-' + (id-1) + '-button-add').style.display = "";
        document.getElementById('generator-' + (id-1) + '-button-remove').style.display = "";
    }

    document.getElementById('generator-' + id + '-container').innerHTML = "";
    generator_counter--;
}

function makeChaosmonkey(id)
{
    var html = ""
         + "<table id='chaosmonkey-" + id + "-table' class='chaosmonkey-table chaosmonkey-color'>"
           + "<tr>"
             + "<td>"
               + "Service:"
             + "</td>"
             + "<td>"
               + "<select class='microservice-names' id='chaosmonkey-" + id + "-service' onchange=\"createJson();\"></select>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Instances:"
             + "</td>"
             + "<td>"
               + "<input id='chaosmonkey-" + id + "-instances' type='number' oninput=\"checkPattern(this, pat_int);createJson();\"/>"
             + "</td>"
           + "</tr>"
           + "<tr>"
             + "<td>"
               + "Time:"
             + "</td>"
             + "<td>"
               + "<input id='chaosmonkey-" + id + "-time' type='number' oninput=\"checkPattern(this, pat_double);createJson();\"/>"
             + "</td>"
           + "</tr>"
         + "</table>"
          + "<br>"
          + "<button id='chaosmonkey-" + id + "-button-add' onclick=\"makeChaosmonkey(" + (id+1) + ");createJson();\">Add Chaosmonkey</button>"
          + "<button id='chaosmonkey-" + id + "-button-remove' onclick=\"removeChaosmonkey(" + id + ");createJson();\">Remove Chaosmonkey</button>"
         + "<div id='chaosmonkey-" + (id+1) + "-container'></div>";

    if(chaosmonkey_counter == 0)
    {
        document.getElementById('chaosmonkey-' + (id-1) + '-button-add').style.display = "none";
    }
    else if(chaosmonkey_counter > 0)
    {
        document.getElementById('chaosmonkey-' + (id-1) + '-button-add').style.display = "none";
        document.getElementById('chaosmonkey-' + (id-1) + '-button-remove').style.display = "none";
    }

    document.getElementById('chaosmonkey-' + id + '-container').innerHTML += html;
    chaosmonkey_counter++;
}

function removeChaosmonkey(id)
{
    if(chaosmonkey_counter == 1)
    {
        document.getElementById('chaosmonkey--1-button-add').style.display = "";
    }
    else if(chaosmonkey_counter > 1)
    {
        document.getElementById('chaosmonkey-' + (id-1) + '-button-add').style.display = "";
        document.getElementById('chaosmonkey-' + (id-1) + '-button-remove').style.display = "";
    }

    document.getElementById('chaosmonkey-' + id + '-container').innerHTML = "";
    chaosmonkey_counter--;
}