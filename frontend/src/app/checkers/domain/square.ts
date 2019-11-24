import {Disk} from "./disk";
import {Position} from "./position";

export declare type SQUARE_TYPE = 'LIGHT' | 'DARK';

export interface Square {
  type: SQUARE_TYPE;
  disk: Disk;
  position: Position;
}
