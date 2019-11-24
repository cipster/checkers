import {Disk} from "./disk";
import {Square} from "./square";

export interface Board {
  id: string;
  blackPieces: Disk[];
  redPieces: Disk[];
  squares: Square[];
}
