<script lang="ts">
  import * as Collapsible from "$lib/components/ui/collapsible/index.js";
  import Spinner from "$lib/components/spinner.svelte";
  import ChevronRight from "lucide-svelte/icons/chevron-right";
  import Button from "$lib/components/ui/button/button.svelte";
  import SquareTerminal from "lucide-svelte/icons/square-terminal";
  import CodeXML from "lucide-svelte/icons/code-xml";
  import Code from "lucide-svelte/icons/code";
  import Logs from "lucide-svelte/icons/logs";

  export let k8sItem: string;
  export let option: string;
  export let details: string = "";
  export let logs: string = "";
  export let execReq: string = "";
  export let execRes: string = "";
  export let loading: boolean = false;
  export let open: boolean = false;
  export let containerHeight = 300;
  export let getDetails: () => Promise<string> 
  export let getLogs: () => Promise<string> = async () => "";
  export let exec: (execReq: string) => Promise<string> = async () => "";

  let isResizing = false;
  const maxContainerHeight = 500;
  const minContainerHeight = 100;
  
  const handleDetails = async () => {
    loading = true;
    option = "DETAILS";
    details = await getDetails();
    loading = false;
  }

  const handleEdit = async () => {
    loading = true;
    option = "EDIT";
    details = await getDetails();
    loading = false;
  }

  const handleLogs = async () => {
    loading = true;
    option = "LOGS";
    details = await getLogs();
    loading = false;
  }

  const handleMouseDown = () => {
    isResizing = true;
  };

  const handleMouseMove = (event: MouseEvent) => {
    if (isResizing) {
      const newHeight = containerHeight - event.movementY;

      // Eğer yeni yükseklik, maksimum yüksekliği geçmiyorsa ve minimum yüksekliğin altında değilse ayarlıyoruz
      if (newHeight <= maxContainerHeight && newHeight >= minContainerHeight) {
        // Yüksekliği artırma koşulu
        if (newHeight < containerHeight) {
          // Yukarı doğru kaydırma engeli
          const isAtTop = window.innerHeight - (containerHeight - event.movementY) <= 150; // Değiştirilebilir yükseklik sınırı
          if (!isAtTop) {
            containerHeight = newHeight;
          }
        } else {
          containerHeight = newHeight;
        }
      }
    }
  };

  const handleMouseUp = () => {
    isResizing = false;
  };

  // Mouse event listener'larını pencereden çıkartıyoruz
  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('mouseup', handleMouseUp);


</script>

<Collapsible.Root class="group/collapsible rounded-t-md bg-sidebar" {open}>
  <div class="flex items-center px-3 py-1 gap-2">
    <Collapsible.Trigger>
      <ChevronRight
        class="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90 size-4"
      />
    </Collapsible.Trigger>
    <h1 style="font-family: 'Courier New', Courier, monospace;">
      {#if (k8sItem && option == "LOGS")}
      {k8sItem} [logs]
      {:else if (k8sItem && option == "TERMINAL")}
      {k8sItem} [sh]
      {:else if (k8sItem && option == "DETAILS" || option == "EDIT")}
      {k8sItem}.yaml {option == "DETAILS" ? "[view]" : "[edit]"}
      {/if}
    </h1>
    
    {#if (k8sItem)}
    <Button
    variant="ghost"
    class="ms-auto bg-muted rounded-lg p-2 h-auto"
    aria-label="Playground"
    onclick={handleDetails}
    >
      <CodeXML class="size-5" />
    </Button>

    <Button
    variant="ghost"
    class="bg-muted rounded-lg p-2 h-auto"
    aria-label="Playground"
    onclick={handleEdit}
    >
      <Code class="size-5" />
    </Button>

    <Button
    variant="ghost"
    class="bg-muted rounded-lg p-2 h-auto"
    aria-label="Playground"
    onclick={handleLogs}
    >
      <Logs class="size-5" />
    </Button>

    <Button
    variant="ghost"
    class="bg-muted rounded-lg p-2 h-auto"
    aria-label="Playground"
    onclick={() => {option = "TERMINAL"}}
    >
      <SquareTerminal class="size-5" />
    </Button>
    {/if}

  </div>

  <Collapsible.Content>
    <div class="resize-handle" on:mousedown={handleMouseDown} style="cursor: ns-resize; height: 5px; background: gray; left: 0; right: 0;"></div>
    <div class="relative text-white log-container rounded-b-md bg-black" style="height: {containerHeight}px;">
        {#if (loading)}
        <div class="flex justify-center items-center h-full">
          <Spinner />
        </div>
        {:else if (option === "LOGS")}
        <div class="w-full h-full p-5 overflow-auto">{logs}</div>
        {:else if (option === "TERMINAL")}
        <div class="flex flex-col justify-between h-full p-5">
          {execRes}
          <div class="flex items-center w-full overflow-auto">
            > 
            <form on:submit={async () => execRes = await exec(execReq)} class="flex items-center w-full">
              <input type="text" bind:value={execReq} class="w-full bg-black text-white resize-none outline-none no-animation"/>
            </form>
          </div>
        </div>
        {:else if (option === "DETAILS")}
        <div class="w-full h-full p-5 overflow-auto">{details}</div>
        {:else if (option === "EDIT")}
        <textarea class="w-full h-full p-5 bg-black overflow-auto resize-none">{details}</textarea>
        {/if}
    </div>
  </Collapsible.Content>
</Collapsible.Root>

<style>
  .log-container {
    font-family: 'Courier New', Courier, monospace;
    white-space: pre-wrap;
    overflow-wrap: break-word;
  }
</style>