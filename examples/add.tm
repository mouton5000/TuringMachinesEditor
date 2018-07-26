{
  "tapes": {
    "tapes": [
      {
        "cells": [
          {
            "symbol": "1",
            "line": 0,
            "column": 0
          },
          {
            "symbol": "0",
            "line": 0,
            "column": 1
          },
          {
            "symbol": "1",
            "line": 0,
            "column": 2
          },
          {
            "symbol": "0",
            "line": 0,
            "column": 3
          },
          {
            "symbol": "1",
            "line": 0,
            "column": 4
          }
        ],
        "topBound": 0,
        "bottomBound": 0,
        "heads": [
          {
            "color": "0x000000ff",
            "line": 0,
            "column": 0
          }
        ],
        "rightBound": "inf",
        "leftBound": "inf"
      }
    ],
    "symbolsMenu": [
      "0",
      "1"
    ]
  },
  "options": {
    "animationDuration": 50,
    "maximumNonDeterministicSearch": 10000
  },
  "graph": {
    "transitions": [
      {
        "output": 1,
        "control2X": 847.2792206135786,
        "input": 1,
        "control1Y": 337.27922061357856,
        "control1X": 592.7207793864214,
        "control2Y": 337.27922061357856,
        "display": {
          "readSymbols": [
            [
              [
                "1"
              ]
            ]
          ],
          "actions": [
            {
              "color": "0x000000ff",
              "actionSymbol": "0"
            },
            {
              "color": "0x000000ff",
              "actionSymbol": "⇐"
            }
          ]
        }
      },
      {
        "output": 0,
        "control2X": 657.2792206135786,
        "input": 0,
        "control1Y": 337.27922061357856,
        "control1X": 402.72077938642144,
        "control2Y": 337.27922061357856,
        "display": {
          "readSymbols": [
            [
              [
                "0",
                "1"
              ]
            ]
          ],
          "actions": [
            {
              "color": "0x000000ff",
              "actionSymbol": "⇒"
            }
          ]
        }
      },
      {
        "output": 2,
        "control2X": 882.5,
        "input": 1,
        "control1Y": 210,
        "control1X": 787.5,
        "control2Y": 210,
        "display": {
          "readSymbols": [
            [
              [
                "0",
                "∅"
              ]
            ]
          ],
          "actions": [
            {
              "color": "0x000000ff",
              "actionSymbol": "1"
            }
          ]
        }
      },
      {
        "output": 1,
        "control2X": 662.5,
        "input": 0,
        "control1Y": 210,
        "control1X": 587.5,
        "control2Y": 210,
        "display": {
          "readSymbols": [
            [
              [
                "∅"
              ]
            ]
          ],
          "actions": [
            {
              "color": "0x000000ff",
              "actionSymbol": "⇐"
            }
          ]
        }
      }
    ],
    "states": [
      {
        "isAccepting": false,
        "x": 530,
        "name": "A",
        "isInitial": true,
        "y": 210,
        "isFinal": false
      },
      {
        "isAccepting": false,
        "x": 720,
        "name": "B",
        "isInitial": false,
        "y": 210,
        "isFinal": false
      },
      {
        "isAccepting": false,
        "x": 950,
        "name": "C",
        "isInitial": false,
        "y": 210,
        "isFinal": true
      }
    ]
  }
}