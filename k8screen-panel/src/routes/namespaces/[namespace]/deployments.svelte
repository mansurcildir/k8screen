<script lang="ts">
  import Bar from "$lib/components/bar.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { Deployment } from "$lib/model/Deployment";
  import { deploymentAPI } from "$lib/service/deployment-service";

  export let namespace; 
  let loading = true;

  let deployments: Deployment[] = [];

  $: if (namespace) {
    getAllDeployments();
  }

	const getAllDeployments = async () => {
		try {
      loading = true;
			deployments = await deploymentAPI.getAllPods(namespace);
		} finally  {
      loading = false;
		}
	}


</script>

<Table.Root>
  <Table.Header>
   <Table.Row>
    <Table.Head>NAME</Table.Head>
    <Table.Head>REPLICAS</Table.Head>
    <Table.Head>UP-TO-DATE</Table.Head>
    <Table.Head>AVAILABLE</Table.Head>
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
   {#each deployments as deployment}
   <Table.Row>
    <Table.Cell>{deployment.name}</Table.Cell>
    <Table.Cell>{deployment.ready_replicas}/{deployment.total_replicas}</Table.Cell>
    <Table.Cell>{deployment.up_to_date}</Table.Cell>
    <Table.Cell>{deployment.available}</Table.Cell>
    <Table.Cell>{deployment.age}</Table.Cell>
   </Table.Row>
   {/each}
   {/if}
  </Table.Body>
 </Table.Root>