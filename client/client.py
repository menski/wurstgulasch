#!/usr/bin/env python3

import requests

DOMAIN = 'camunda.example.com'
PORT = 8080

DEFINITION_API = \
    'http://{2}.definition.{0}:{1}/engine-rest'
PROCESS_API = \
    'http://{2}.process.{0}:{1}/engine-rest'
TASK_API = \
    'http://{2}.task.{0}:{1}/engine-rest'


## URLS
def format_url(url, *args):
    return url.format(DOMAIN, PORT, *args)


def definition_url(processDefinitionKey):
    url = DEFINITION_API + '/process-definition/key/{2}'
    return format_url(url, processDefinitionKey)


def process_url(processInstanceId):
    url = PROCESS_API + '/process-instance/{2}'
    return format_url(url, processInstanceId)


def task_url(taskId):
    url = TASK_API + '/task/{2}'
    return format_url(url, taskId)


def tasks_url(processInstanceId):
    url = PROCESS_API + '/task'
    return format_url(url, processInstanceId)


## payloads
def payload_variable(value, variableType='String'):
    return {'value': value, 'type': variableType}


def payload_invoice(amount, creditor, invoiceNumber):
    variables = {}
    variables['amount'] = payload_variable(amount)
    variables['creditor'] = payload_variable(creditor)
    variables['invoiceNumber'] = payload_variable(invoiceNumber)
    return {'variables': variables}


def payload_approver(approver):
    variables = {}
    variables['approver'] = payload_variable(approver)
    return {'variables': variables}


def payload_approved(approved):
    variables = {}
    variables['approved'] = payload_variable(approved, 'Boolean')
    return {'variables': variables}


## REST calls
def post(url, json=None):
    return requests.post(url, json=json)


def log_status(message, resource, response):
    status = response.status_code
    print('{} <{}>: {}'.format(message, resource, status))


def start_process(processDefinitionKey, json=None):
    url = definition_url(processDefinitionKey) + '/start'
    response = post(url, json)
    log_status('start process', processDefinitionKey, response)
    return response


def get_tasks(processInstanceId):
    url = tasks_url(processInstanceId)
    query = {'processInstanceId': processInstanceId}
    response = post(url, query)
    log_status('get tasks', processInstanceId, response)
    return response


def complete_task(taskId, json=None):
    url = task_url(taskId) + '/complete'
    response = post(url, json)
    log_status('complete task', taskId, response)
    return response

## test
if __name__ == '__main__':
    # start process instance
    payload = payload_invoice('$1337', 'Ola Bar', 'IN-VOI-CE')
    res = start_process('invoice', payload)
    processId = res.json()['id']

    # get tasks of process instance
    res = get_tasks(processId)
    taskIds = [task['id'] for task in res.json()]

    # complete task
    payload = payload_approver('demo')
    complete_task(taskIds[0], payload)

    # get tasks of process instance
    res = get_tasks(processId)
    taskIds = [task['id'] for task in res.json()]

    # complete task
    payload = payload_approved(True)
    res = complete_task(taskIds[0], payload)
