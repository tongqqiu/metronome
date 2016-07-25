---
title: Metronome
---

# Overview

Metronome is a Mesos framework for scheduling jobs. Metronome comes included with DC/OS version 1.8 and later. You can use Metronome to create Jobs in DC/OS using the DC/OS web interface, the DC/OS CLI, or through the Metronome API.

## Functionality

You can create a Metronome job as a single command you include when you create the job, or you can point to a Docker image.

When you create the Metronome job, you can specify:

* The amount of CPU your job will consume.

* The amount of memory youd job will consume.

* The disk space your job will consume.

* The schedule for your job, in chron format. You can also set the time zone and starting deadline.

* An arbitrary number of labels to attach to your job.
