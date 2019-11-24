import {Disk} from "./disk";
import {Position} from "./position";

export declare type MOVE_TYPE = 'SLIDE' | 'JUMP';

export interface Move {
  disk: Disk;
  capturedDisk?: Disk;
  type?: MOVE_TYPE;
  from: Position;
  destination: Position;
}
