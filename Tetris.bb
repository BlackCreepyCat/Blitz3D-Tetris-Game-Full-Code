;tetris clone
;by WolRon

;set up graphics
Graphics 800, 600 ,32 ,2
SetBuffer BackBuffer()

;seed random number generator
SeedRnd MilliSecs()

;set up arrays
Dim Board(9, 22)
Dim BlockColor(7, 2)
Dim Piece(28, 3, 3)
Dim LineToErase(4)

;read in block color data
For iter = 1 To 7
  Read BlockColor(iter, 0)
  Read BlockColor(iter, 1)
  Read BlockColor(iter, 2)
Next
Data 255, 255, 0;yellow
Data 0, 128, 255;turquoise
Data 0, 0, 255  ;blue
Data 255, 128, 0;orange
Data 255, 0, 0  ;red
Data 0, 255, 0  ;green
Data 128, 0, 255;purple

;read in piece descriptions
For iter = 1 To 28
  For yiter = 0 To 3
    For xiter = 0 To 3
      Read Piece(iter, xiter, yiter)
    Next
  Next
Next

;piece descriptions
Data 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;square
Data 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 ;line
Data 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;left L
Data 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;right L
Data 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;left s
Data 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;right s
Data 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;tee up
;rotated 90 degrees cw
Data 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;square
Data 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0 ;line rot 1
Data 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;left L rot 1
Data 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0 ;right L rot 1
Data 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 ;left s sideways
Data 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;right s sideways
Data 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;tee right
;rotated 180 degrees cw
Data 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;square
Data 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 ;line rot 2
Data 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0 ;left L rot 2
Data 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 ;right L rot 2
Data 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;left s
Data 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;right s
Data 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;tee down
;rotated 270 degrees cw
Data 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ;square
Data 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 ;line rot 3
Data 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 ;left L rot 3
Data 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;right L rot 3
Data 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 ;left s sideways
Data 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;right s sideways
Data 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 ;tee left

;set origins
Global OrigBoardX      = 210  ;board origin
Global OrigBoardY      = 5
Global OrigLevelLinesX = 500  ;level & lines origin
Global OrigLevelLinesY = 186  
Global OrigNextPieceX  = 600  ;next piece origin
Global OrigNextPieceY  = 75
Global OrigMessAreaX   = 500  ;message area origin
Global OrigMessAreaY   = 300
Global OrigKeyMapX     = 0    ;key mapping text origin
Global OrigKeyMapY     = 300

;declare/initialize global variables
Global Level
Global Lines
Global EndOfGame = True
Global CurrentPiece
Global CurPieceX
Global CurPieceY
Global CurPieceRot
Global NextPiece
Global GhostPieceY
Global RepeatSpeed
Global RepeatDelay
Global NowTime
Global LastDropTime
Global DropSpeed
Global DropDelay
Global GamePaused
Global PauseTime
Global NumLines
Global ClearLines
Global BlockToErase#
Global Points
Global PointColorFade
Global LevelUpColorFade

;declare key globals
Global KeyEsc
Global KeyPause
Global KeyRotate
Global KeyRotateLeft
Global KeyLeft
Global KeyRight
Global KeyDwn
Global KeyInstantDrop
Global KeyN
Global KeyQ


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;   Main Game loop   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Repeat
  Cls
  DrawBoard()
  DrawInfo()
  If GamePaused
    DrawPauseMessage()
  Else
    DrawBoardBlocks()
    DrawNextPiece()
    If Not ClearLines
      If Not EndOfGame Then DrawGhostPiece()
      DrawCurrentPiece()
    EndIf
  EndIf
  ProcessInputs()
  If Not EndOfGame
    If Not ClearLines
      If Not GamePaused
        If KeyDwn
          DropDelay = 35
        Else
          DropDelay = DropSpeed
        EndIf
        NowTime = MilliSecs()
        If (NowTime > LastDropTime + DropDelay) Or KeyInstantDrop
          LastDropTime = NowTime
          If Not DropPiece()
            If Not PlacePiece()
              EndGame()
            EndIf
          EndIf
        EndIf
      EndIf
    Else
      LineClearer()
    EndIf
  Else
    PrintGameOver()
  EndIf

  Flip
Until KeyHit(1);Esc key
End
;;;;;;;;;;;;;;;;;;;;;;;;;;;;   End of main loop   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;   Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function ProcessInputs()
  KeyEsc = KeyHit(1)          ;Esc key
  KeyPause = KeyHit(25) Or KeyHit(197);P key, Pause key
  KeyRotate = KeyHit(57) Or KeyHit(45);spacebar, X key
  KeyRotateLeft = KeyHit(44)      ;Z key
  KeyLeft = KeyDown(203)        ;left key
  KeyRight = KeyDown(205)        ;right key
  KeyDwn = KeyDown(208)        ;down key
  KeyInstantDrop = KeyHit(200)    ;up key
  KeyN = KeyHit(49)          ;N key
  KeyQ = KeyHit(16)          ;Q key
  
  If Not EndOfGame
    If KeyRotate Then RotatePiece() ;space key or X key
    If KeyRotateLeft Then RotatePiece(True); Z key
    If (Not KeyLeft) And (Not KeyRight)
      RepeatDelay = True
    Else
      If RepeatSpeed < NowTime
        If RepeatDelay
          RepeatSpeed = NowTime + 150
          RepeatDelay = False
        Else
          RepeatSpeed = NowTime + 30
        EndIf
        If keyLeft Then MoveLeft()
        If keyRight Then MoveRight()
      EndIf
    EndIf
    If KeyInstantDrop Then CurPieceY = GhostPieceY
    If KeyEsc Or KeyPause
      If GamePaused
        GamePaused = False
        LastDropTime = LastDropTime + MilliSecs() - PauseTime
      Else
        GamePaused = True
        PauseTime = MilliSecs()
      EndIf
    EndIf
    If KeyQ Then EndGame()  ;quit game
  Else
    If KeyN Then StartNewGame()  ;new game
    If KeyEsc Then End
  EndIf
End Function

Function DrawBoardBlocks()
  ;draw blocks
  Origin OrigBoardX, OrigBoardY
  For yiter = 0 To 22
    For xiter = 0 To 9
      If Board(xiter, yiter) <> 0
        r = BlockColor(Board(xiter, yiter), 0)
        g = BlockColor(Board(xiter, yiter), 1)
        b = BlockColor(Board(xiter, yiter), 2)
        DrawBlk(r, g, b, xiter*25, yiter*25, .25)
      EndIf
    Next
  Next
End Function

Function DrawBoard()
  ;draw border
  Origin OrigBoardX, OrigBoardY + 75
  Color 255, 0, 0
  Rect -3, -3, 255, 505, 0
  ;draw grid lines on board
  Color 50, 50, 50
  For yiter = 0 To 20
    Line 0, yiter*25-1, 250, yiter*25-1
  Next
  For xiter = 0 To 10
    Line xiter*25-1, 0, xiter*25-1, 500 
  Next
End Function

Function DrawGhostPiece()
  Origin OrigBoardX, OrigBoardY
  r = BlockColor(CurrentPiece, 0) * .15
  g = BlockColor(CurrentPiece, 1) * .15
  b = BlockColor(CurrentPiece, 2) * .15
  For yiter = 0 To 3
    For xiter = 0 To 3
      If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter)
        DrawBlk(r, g, b, CurPieceX*25 + xiter*25, GhostPieceY*25 + yiter*25, 1)
      EndIf
    Next
  Next
End Function

Function DrawCurrentPiece()
  Origin OrigBoardX, OrigBoardY
  r = BlockColor(CurrentPiece, 0)
  g = BlockColor(CurrentPiece, 1)
  b = BlockColor(CurrentPiece, 2)
  For yiter = 0 To 3
    For xiter = 0 To 3
      If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter)
        DrawBlk(r, g, b, CurPieceX*25 + xiter*25, CurPieceY*25 + yiter*25, 1)
      EndIf
    Next
  Next
End Function

Function DrawNextPiece()
  Origin OrigNextPieceX, OrigNextPieceY
  r = BlockColor(NextPiece, 0)
  g = BlockColor(NextPiece, 1)
  b = BlockColor(NextPiece, 2)
  For yiter = 0 To 3
    For xiter = 0 To 3
      If Piece(NextPiece, xiter, yiter)
        DrawBlk(r, g, b, xiter*25, yiter*25, .25)
      EndIf
    Next
  Next
End Function

Function DrawBlk(r, g, b, x, y, fade#)
  Color r, g, b
  Rect x, y, 24, 24
  Color r * fade#, g * fade#, b * fade#
  Rect x + 2, y + 2, 20, 20, 0
  Line x, y, x+2, y+2
  Line x, y+23, x+23, y+23
  Line x+1, y+22, x+22, y+22
  Line x+22, y+1, x+22, y+22
  Line x+23, y, x+23, y+23
End Function

Function DrawInfo()
  Origin OrigLevelLinesX, OrigLevelLinesY
  Color 128, 255, 150
  Text 40, -95, "Next:"
  Text 0, 0, "Level:" + RSet$(Str$(Level), 4)
  Text 0, 40, "Lines:" + RSet$(Str$(Lines), 4)
  If PointColorFade > 0
    Color PointColorFade, PointColorFade, 0
    Text 65, 55, "- " + Str$(Points), True
    PointColorFade = PointColorFade - 1
  EndIf
  If LevelUpColorFade > 0
    Color LevelUpColorFade, LevelUpColorFade, 0
    Text 65, 15, "Level Up!", True
    LevelUpColorFade = LevelUpColorFade - 1
  EndIf
  If (Not EndOfGame) And (Not GamePaused)
    Color 255, 255, 0
    Text 150, 300, "Press P to pause", True
  EndIf
  Origin OrigKeyMapX, OrigKeyMapY
  Color 0, 0, 255
  Text 5, 40, " Left arrow-move left"
  Text 5, 60, "Right arrow-move right"
  Text 5, 80, " Down arrow-drop fast"
  Text 5, 100, "   Up arrow-instant drop"
  Text 5, 120, "Spacebar, X-rotate right"
  Text 5, 140, "          Z-rotate left"
End Function

Function DropPiece()
  For yiter = 3 To 0 Step -1
    For xiter = 0 To 3
      If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter) <> 0
        If CurPieceY+yiter+1 > 22 Then Return False
        If Board(CurPieceX+xiter, CurPieceY+yiter+1) <> 0 Then Return False
      EndIf
    Next
  Next
  CurPieceY = CurPieceY + 1
  CalcGhostPosition()
  Return True
End Function

Function PlacePiece()
  Local outofbounds
  For yiter = 0 To 3
    For xiter = 0 To 3
      If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter) <> 0
        Board(CurPieceX+xiter, CurPieceY+yiter) = CurrentPiece
        If CurPieceY+yiter < 3 Then outofbounds = True;above the 10x20 playing area
      EndIf
    Next
  Next
  If outofbounds Then Return False
  ;check if any lines were created
  CheckForLines()
  If Not ClearLines
    ResetPiece()
  EndIf
  Return True
End Function

Function CheckForLines()
  Local blockcount
  NumLines = 0
  For yiter = 22 To 3 Step -1
    For xiter = 0 To 9
      If Board(xiter, yiter) <> 0 Then blockcount = blockcount + 1
    Next
    If blockcount = 10
      NumLines = NumLines + 1
      LineToErase(NumLines) = yiter
    EndIf
    blockcount = 0
  Next
  ;1 line  = 1 point
  ;2 lines = 3 points
  ;3 lines = 5 points
  ;4 lines = 8 points
  If NumLines = 0 Then Return
  ClearLines = True
  BlockToErase = 5
  Points = (NumLines + (NumLines - 1)) + (1 * (NumLines = 4))
  Lines = Lines - Points
  PointColorFade = 255
  ;if cleared 5 lines (per level) then level up
  If Lines < 1
    DropSpeed = DropSpeed - 50 + (50 * (DropSpeed < 50))
    Level = Level + 1
    Lines = Level * 5
    LevelUpColorFade = 255
  EndIf
End Function

Function LineClearer()
  ;clear one block at a time from center out
  If BlockToErase > 0
    For iter = 1 To NumLines
      Board(BlockToErase - 1, LineToErase(iter)) = 0
      Board(10 - BlockToErase, LineToErase(iter)) = 0
    Next
    BlockToErase = BlockToErase - .5 ;using .5 just to make it clear slower
  Else
    ;shift blocks down
    For iter = 1 To NumLines
      For yiter = (LineToErase(iter) + iter - 1) To 3 Step -1
        For xiter = 0 To 9
          Board(xiter, yiter) = Board(xiter, yiter - 1)
        Next
      Next
    Next
    ClearLines = False
    ResetPiece()
  EndIf
End Function

Function MoveLeft()
  For xiter = 0 To 3
    For yiter = 0 To 3
      If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter) <> 0
        If CurPieceX+xiter-1 < 0 Then Return
        If Board(CurPieceX+xiter-1, CurPieceY+yiter) <> 0 Then Return
      EndIf
    Next
  Next
  CurPieceX = CurPieceX - 1
  CalcGhostPosition()
End Function

Function MoveRight()
  For xiter = 3 To 0 Step -1
    For yiter = 0 To 3
      If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter) <> 0
        If CurPieceX+xiter+1 > 9 Then Return
        If Board(CurPieceX+xiter+1, CurPieceY+yiter) <> 0 Then Return
      EndIf
    Next
  Next
  CurPieceX = CurPieceX + 1
  CalcGhostPosition()
End Function

Function RotatePiece(rotateLeft = False)
  Local pieceToCheck
  If rotateLeft
    pieceToCheck = CurrentPiece + (((CurPieceRot - 1) + 4 * (CurPieceRot < 1)) * 7)
  Else
    pieceToCheck = CurrentPiece + (((CurPieceRot + 1) * (CurPieceRot < 3)) * 7)
  EndIf
  For yiter = 0 To 3
    For xiter = 0 To 3
      If Piece(pieceToCheck, xiter, yiter) <> 0
        If CurPieceX+xiter < 0 Or CurPieceX+xiter > 9 Or CurPieceY+yiter > 22 Then Return
        If Board(CurPieceX+xiter, CurPieceY+yiter) <> 0 Then Return
      EndIf
    Next
  Next
  If rotateLeft
    CurPieceRot = (CurPieceRot - 1) + 4 * (CurPieceRot < 1)
  Else
    CurPieceRot = (CurPieceRot + 1) * (CurPieceRot < 3)
  EndIf
  CalcGhostPosition()
End Function

Function CalcGhostPosition()
  GhostPieceY = CurPieceY
  Repeat
    For yiter = 3 To 0 Step -1
      For xiter = 0 To 3
        If Piece(CurrentPiece + (CurPieceRot * 7), xiter, yiter) <> 0
          If GhostPieceY+yiter+1 > 22 Then Return
          If Board(CurPieceX+xiter, GhostPieceY+yiter+1) <> 0 Then Return
        EndIf
      Next
    Next
    GhostPieceY = GhostPieceY + 1
  Forever
End Function

Function DrawPauseMessage()
  Origin OrigMessAreaX, OrigMessAreaY
  Color 255, 255, 0
  Text 150, 200, "P = Unpause", True, True
  Text 150, 225, "Q = End game", True, True
  Color 255, 0, 0
  Rect 80, 70, 140, 60, 0
  Color 255, 255, 0
  Text 150, 100, "Paused", True, True
End Function

Function EndGame()
  EndOfGame = True
  GamePaused = False
End Function

Function PrintGameOver()
  Origin OrigMessAreaX, OrigMessAreaY
  Color 255, 0, 0
  Text 150, 50, "Game Over", True, True
  Color 255, 255, 0
  Text 150, 100, "Press 'N' to", True, True
  Text 150, 120, "start a new game.", True, True
  Text 150, 230, "Press ESC to quit.", True, True
End Function

Function StartNewGame()
  ;reset variables
  NextPiece = Rand(1, 7)
  ResetPiece()
  DropSpeed = 735
  Level = 1
  Lines = 5
  EndOfGame = False
  GamePaused = False
  ;erase board
  For yiter = 0 To 22
    For xiter = 0 To 9
      Board(xiter, yiter) = 0
    Next
  Next
  ;set time dependent variables
  LastDropTime = MilliSecs()
End Function

Function ResetPiece()
  ;reset piece to top center
  CurPieceX = 3
  CurPieceY = 0
  CurPieceRot = 0
  CurrentPiece = NextPiece
  NextPiece = Rand(1, 7)
  CalcGhostPosition()
End Function


;~IDEal Editor Parameters:
;~C#Blitz3D