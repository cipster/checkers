import {Position} from "./position";

export declare type DISK_COLOR = 'BLACK' | 'RED';
export declare type DISK_STATE = 'IN_PLAY' | 'CAPTURED';

export interface Disk {
  id: string;
  color: DISK_COLOR;
  state: DISK_STATE;
  currentPosition: Position;
  king: boolean;
}
