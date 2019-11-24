import {Board} from "./board";
import {Disk} from "./disk";
import {Turn} from "./turn";

export declare type GAME_STATUS = 'ONGOING' | 'FINISHED';

export interface Game {
  id: string;
  board: Board;
  currentTurn: Turn;
  playedTurns: Turn[];
  capturedDisks: Disk[];
  status: GAME_STATUS;
}
