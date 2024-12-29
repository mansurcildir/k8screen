export interface Service {
  name: string;
  type: string;
  cluster_ip: string;
  external_ip: string;
  ports: string[];
  age: string;
}
