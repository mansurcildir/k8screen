<script lang="ts">
    import Bar from "$lib/components/bar.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { Service } from "$lib/model/Service";
  import { serviceAPI } from "$lib/service/service-service";

  export let namespace;
  let loading = true;

  let services: Service[] = [];

  $: if (namespace) {
    getAllServices();
  }

	const getAllServices = async () => {
		try {
      loading = true;
      services = await serviceAPI.getAllServices(namespace);
		} finally  {
      loading = false;
		}
	}


</script>

<Table.Root>
  <Table.Header>
   <Table.Row>
    <Table.Head>NAME</Table.Head>
    <Table.Head>TYPE</Table.Head>
    <Table.Head>CLUSTER-IP</Table.Head>
    <Table.Head>EXTERNAL-IP</Table.Head>
    <Table.Head>PORTS</Table.Head>
    <Table.Head>AGE</Table.Head>
   </Table.Row>
  </Table.Header>
  <Table.Body>
    {#if (loading)}
    <Table.Row>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
    </Table.Row>
    {:else}
    {#each services as service}
    <Table.Row>
      <Table.Cell>{service.name}</Table.Cell>
      <Table.Cell>{service.type}</Table.Cell>
      <Table.Cell>{service.cluster_ip}</Table.Cell>
      <Table.Cell>{service.external_ip}</Table.Cell>
      <Table.Cell>{service.ports}</Table.Cell>
      <Table.Cell>{service.age}</Table.Cell>
     </Table.Row>
   {/each}
   {/if}
  </Table.Body>
 </Table.Root>