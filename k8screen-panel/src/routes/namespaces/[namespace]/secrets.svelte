<script lang="ts">
    import Bar from "$lib/components/bar.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { Secret } from "$lib/model/Secret";
  import { secretAPI } from "$lib/service/secret-service";

  export let namespace;
  let loading = true;

  let secrets: Secret[] = [];

  $: if (namespace) {
    getAllSecrets();
  }

	const getAllSecrets = async () => {
		try {
      loading = true;
      secrets = await secretAPI.getAllSecrets(namespace);
		} finally  {
      loading = false;
		}
	}

</script>

<Table.Root>
  <Table.Caption>List of {namespace} secrets.</Table.Caption>
  <Table.Header>
   <Table.Row>
    <Table.Head>NAME</Table.Head>
    <Table.Head>TYPE</Table.Head>
    <Table.Head>DATA</Table.Head>
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
    </Table.Row>
    {:else}
    {#each secrets as secret}
    <Table.Row>
      <Table.Cell>{secret.name}</Table.Cell>
      <Table.Cell>{secret.type}</Table.Cell>
      <Table.Cell>{secret.data_size}</Table.Cell>
      <Table.Cell>{secret.age}</Table.Cell>
   </Table.Row>
   {/each}
   {/if}
  </Table.Body>
 </Table.Root>