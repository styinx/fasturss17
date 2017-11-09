var services = 1;
var architecture = {};

function checkPattern(element, pattern)
{
    if(pattern.test(pattern))
    {
        element.style.border = "1px solid green";
    }
    else
    {
        element.style.border = "1px solid red";
    }
}

function showById(id)
{
    if(document.getElementById(id) !== null)
        document.getElementById(id).style.display = "";
}

function hideById(id)
{
    if(document.getElementById(id) !== null)
        document.getElementById(id).style.display = "none";
}

function makeMicroservice(id)
{
    var html = "" +
    "        <table id='mstb" + id + "' class='ms-color'>" +
    "            <tr>" +
    "                <td>Name:</td>" +
    "                <td><input id='ms-name-" + id + "' type='text'/></td>" +
    "            </tr>" +
    "            <tr>" +
    "                <td>Instances:</td>" +
    "                <td><input id='ms-instances-" + id + "' type='number'/></td>" +
    "            </tr>" +
    "            <tr>" +
    "                <td>Capacity:</td>" +
    "                <td><input id='ms-capacity-" + id + "' type='number'/></td>" +
    "            </tr>" +
    "            <tr>" +
    "                <td>Pattern:" +
    "                    <select id='ms-pattern-" + id + "'>" +
    "                        <option value=''>none</option>" +
    "                        <option value='thread-pool'>Thread Pool</option>" +
    "                        <option value='thread-pool'>Thread Queue</option>" +
    "                    </select>" +
    "                </td>" +
    "                <td>Value: <input type='number'/></td>" +
    "            </tr>" +
    "            <tr><td colspan='100%'>&nbsp;</td></tr>" +
    "            <tr>" +
    "                <td colspan='100%'><b>Operations:</b></td>" +
    "            </tr>" +
    "            <tr>" +
    "                <td>&nbsp;</td>" +
    "                <td>" +
    "                    <table class='op-color'>" +
    "                        <tr>" +
    "                            <td>Name:</td>" +
    "                            <td><input type='text'/></td>" +
    "                        </tr>" +
    "                        <tr>" +
    "                            <td>Pattern:</td>" +
    "                            <td>" +
    "                                <select>" +
    "                                    <option></option>" +
    "                                    <option>Circuit Breaker</option>" +
    "                                </select>" +
    "                            </td>" +
    "                        </tr>" +
    "                        <tr>" +
    "                            <td>Demand:</td>" +
    "                            <td><input type='number'/></td>" +
    "                        </tr>" +
    "                        <tr>" +
    "                            <td>Dependency:</td>" +
    "                            <td>" +
    "                                <select>" +
    "                                    <option></option>" +
    "                                </select>" +
    "                                <select>" +
    "                                    <option></option>" +
    "                                </select>" +
    "                                <input type='number' min='0' max='10'/>" +
    "                            </td>" +
    "                        </tr>" +
    "                    </table>" +
    "                    <button id='opbt-1'>New Operation</button>" +
    "                    <button id='opbt-2'>Remove Operation</button>" +
    "                </td>" +
    "            </tr>" +
    "        </table>" +
    "        <button id='msbt-1-" + id + "' onclick=\"hideById('msbt-1-" + id + "');makeMicroservice('" + (id+1) + "');\">New Microservice</button>" +
    "        <button id='msbt-2-" + id + "' onclick=\"showById('msbt-1-" + (id-1) + "');hideById('msbt-1-" + id + "');hideById('msbt-2-" + id + "');hideById('mstb" + id + "');\">Remove Microservice</button>" +
    "        <div id='microservice" + (id+1) + "'></div>";
    document.getElementById('microservice'+id).innerHTML += html;
}

function makeOperation(id)
{

}

function makeGenerator(id)
{

}

function makeMonkey(id)
{

}